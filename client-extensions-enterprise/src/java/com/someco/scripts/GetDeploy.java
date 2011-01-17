package com.someco.scripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.WCMAppModel;
import org.alfresco.repo.avm.AVMNodeConverter;
import org.alfresco.repo.avm.actions.AVMDeployWebsiteAction;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.sandbox.SandboxConstants;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO9075;
import org.alfresco.web.bean.wcm.AVMUtil;
import org.alfresco.web.scripts.WebScriptCache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.WebScriptStatus;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.log4j.Logger;

public class GetDeploy extends DeclarativeWebScript {
		
	// Dependencies
	private AVMService avmService;
	private ActionService actionService;
	private NodeService nodeService;
	private PermissionService permissionService;
	private ImporterBootstrap importerBootstrap;
	private SearchService searchService;
	
    private static final String PROP_ROOT_FOLDER = "spaces.company_home.childname";
    private static final String PROP_WCM_FOLDER = "spaces.wcm.childname";
    
	private Logger logger = Logger.getLogger(GetDeploy.class);
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req,
			WebScriptStatus status, WebScriptCache cache) {
		// declare the model object we're going to return
		Map<String, Object> model = new HashMap<String, Object>();

		String store = req.getParameter("s");
		
		// echo back the store that was passed in
		model.put("store", store);
		
		if (store == null || store.length() == 0) {
			status.setCode(400);			
			status.setMessage("Store is a required parameter.");
			status.setRedirect(true);
			return model;
		}

		// check to see if store exists
		// if it does not, an exception will get thrown
		AVMUtil.lookupStoreDNS(store);
		
		String stagingStore = AVMUtil.buildStagingStoreName(AVMUtil.getStoreId(store));
		NodeRef webProjectNodeRef = (NodeRef) avmService.getStoreProperty(stagingStore, 
	                                     SandboxConstants.PROP_WEB_PROJECT_NODE_REF).getValue(DataTypeDefinition.NODE_REF);
       
		if (webProjectNodeRef == null || !nodeService.exists(webProjectNodeRef)) {
			status.setCode(500);			
			status.setMessage("Web project does not exist.");
			status.setRedirect(true);
			return model;			
		}
		
		// if this store is already allocated, use that, otherwise
		// try to find a new server available in the pool
		NodeRef serverRef = null;
		List<NodeRef> allocServerList = findDeployToServers(webProjectNodeRef, store);
		if (allocServerList.isEmpty()) {
			List<NodeRef> serverList = findDeployToServers(webProjectNodeRef, null);
			if (serverList.isEmpty()) {
				status.setCode(500);			
				status.setMessage("No test servers are available.");
				status.setRedirect(true);
				return model;
	        }
			serverRef = serverList.get(0);
		} else {
			serverRef = allocServerList.get(0);
		}
		
		// if the node exists, we're good
        if (nodeService.exists(serverRef)) {
        	// get all properties of the target server
        	Map<QName, Serializable> serverProps = nodeService.getProperties(serverRef);
                 
        	String url = (String)serverProps.get(WCMAppModel.PROP_DEPLOYSERVERURL);
        	String serverUri = AVMDeployWebsiteAction.calculateServerUri(serverProps);
        	String serverName = (String)serverProps.get(WCMAppModel.PROP_DEPLOYSERVERNAME);
        	if (serverName == null || serverName.length() == 0) {
        		serverName = serverUri;
        	}

        	// Allocate the test server to the current sandbox
        	String allocatedTo = (String)serverProps.get(WCMAppModel.PROP_DEPLOYSERVERALLOCATEDTO);
        	if ((allocatedTo != null) && !(allocatedTo.equals(store))) {
        		throw new AlfrescoRuntimeException("testserver.taken", new Object[] {serverName});
        	} else {
        		nodeService.setProperty(serverRef, WCMAppModel.PROP_DEPLOYSERVERALLOCATEDTO, store);
        	}

            // create a deploymentattempt node to represent this deployment
            String attemptId = GUID.generate();
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(8, 1.0f);
            props.put(WCMAppModel.PROP_DEPLOYATTEMPTID, attemptId);
            props.put(WCMAppModel.PROP_DEPLOYATTEMPTTYPE, WCMAppModel.CONSTRAINT_TESTSERVER);
            props.put(WCMAppModel.PROP_DEPLOYATTEMPTSTORE, store);
            props.put(WCMAppModel.PROP_DEPLOYATTEMPTVERSION, "-1");
            props.put(WCMAppModel.PROP_DEPLOYATTEMPTTIME, new Date());
            props.put(WCMAppModel.PROP_DEPLOYATTEMPTSERVERS, serverName);   
            NodeRef attempt = nodeService.createNode(webProjectNodeRef, 
                  WCMAppModel.ASSOC_DEPLOYMENTATTEMPT, WCMAppModel.ASSOC_DEPLOYMENTATTEMPT, 
                  WCMAppModel.TYPE_DEPLOYMENTATTEMPT, props).getChildRef();
            
            // allow anyone to add child nodes to the deploymentattempt node
            permissionService.setPermission(attempt, PermissionService.ALL_AUTHORITIES, 
                     PermissionService.ADD_CHILDREN, true);
        	
            if (logger.isDebugEnabled()) logger.debug("Issuing deployment request for: " + serverName);
                 
            // store the server deployed to in the model
            model.put("serverName", serverName);
            if (url != null) {
            	model.put("serverUrl", url);
            }
             
            String storeRoot = AVMUtil.buildSandboxRootPath(store);
            NodeRef websiteRef = AVMNodeConverter.ToNodeRef(-1, storeRoot);
             
            // create and execute the action asynchronously
            Map<String, Serializable> args = new HashMap<String, Serializable>(1, 1.0f);
            args.put(AVMDeployWebsiteAction.PARAM_WEBPROJECT, webProjectNodeRef);
            args.put(AVMDeployWebsiteAction.PARAM_SERVER, serverRef);
            args.put(AVMDeployWebsiteAction.PARAM_ATTEMPT, attempt);
            Action action = actionService.createAction(AVMDeployWebsiteAction.NAME, args);
            actionService.executeAction(action, websiteRef, false, true);
          
            // set the deploymentattempid property on the store this deployment was for
            avmService.deleteStoreProperty(store, SandboxConstants.PROP_LAST_DEPLOYMENT_ID);
            avmService.setStoreProperty(store, SandboxConstants.PROP_LAST_DEPLOYMENT_ID, 
                      new PropertyValue(DataTypeDefinition.TEXT, attemptId));

        }
        
		return model;
	}

	private List<NodeRef> findDeployToServers(NodeRef webProjectRef, String store) {
        // get folder names   
        Properties configuration = this.importerBootstrap.getConfiguration();
        String rootFolder = configuration.getProperty(PROP_ROOT_FOLDER);
        String wcmFolder = configuration.getProperty(PROP_WCM_FOLDER);
            
        // get web project name
        String webProjectName = (String)this.nodeService.getProperty(
                 webProjectRef, ContentModel.PROP_NAME);
        String safeProjectName = ISO9075.encode(webProjectName); 
        
        // build the query
        StringBuilder query = new StringBuilder("PATH:\"/");
        query.append(rootFolder);
        query.append("/");
        query.append(wcmFolder);
        query.append("/cm:");
        query.append(safeProjectName);
        query.append("/*\" AND @");
        query.append(NamespaceService.WCMAPP_MODEL_PREFIX);
        query.append("\\:");
        query.append(WCMAppModel.PROP_DEPLOYSERVERTYPE.getLocalName());
        query.append(":\"");
        query.append(WCMAppModel.CONSTRAINT_TESTSERVER);
        query.append("\" AND @");
        query.append(NamespaceService.WCMAPP_MODEL_PREFIX);
        query.append("\\:");
        query.append(WCMAppModel.PROP_DEPLOYTYPE.getLocalName());
        query.append(":\"");
        query.append(WCMAppModel.CONSTRAINT_FILEDEPLOY);
        query.append("\"");
        
        // if we got a store, include it
        if (store != null && !store.equals("")) {
        	query.append(" AND @");
            query.append(NamespaceService.WCMAPP_MODEL_PREFIX);
            query.append("\\:");
            query.append(WCMAppModel.PROP_DEPLOYSERVERALLOCATEDTO.getLocalName());
            query.append(":\"");
            query.append(store);
            query.append("\"");
        } else {
        	// otherwise, explicitly search for objects where the attr is NULL
        	query.append(" AND ISNULL:\"");
            query.append(WCMAppModel.PROP_DEPLOYSERVERALLOCATEDTO.toString());
            query.append("\"");
        }
        
        // execute the query
        ResultSet results = null;
        List<NodeRef> servers = new ArrayList<NodeRef>();
        try {
            results = searchService.query(webProjectRef.getStoreRef(), 
                     SearchService.LANGUAGE_LUCENE, query.toString());
         
            for (NodeRef server : results.getNodeRefs()) {
                servers.add(server);
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }

        return servers;
    }

	public void setAvmService(AVMService avmService) {
		this.avmService = avmService;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setImporterBootstrap(ImporterBootstrap importerBootstrap) {
		this.importerBootstrap = importerBootstrap;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}
