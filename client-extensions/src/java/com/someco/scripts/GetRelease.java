package com.someco.scripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.model.ContentModel;
import org.alfresco.model.WCMAppModel;
import org.alfresco.repo.avm.actions.AVMDeployWebsiteAction;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.wcm.sandbox.SandboxConstants;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.alfresco.web.bean.wcm.AVMUtil;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;

public class GetRelease extends DeclarativeWebScript {
	// Dependencies
	private AVMService avmService;
	private NodeService nodeService;
	private ImporterBootstrap importerBootstrap;
	private SearchService searchService;
	
    private static final String PROP_ROOT_FOLDER = "spaces.company_home.childname";
    private static final String PROP_WCM_FOLDER = "spaces.wcm.childname";
    
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req,
			Status status, Cache cache) {
		// declare the model object we're going to return
		Map<String, Object> model = new HashMap<String, Object>();

		String store = req.getParameter("s");
		
		// echo back the store that was passed in
		model.put("store", store);
		
		if (store == null || store.length() == 0) {
			status.setCode(Status.STATUS_BAD_REQUEST, "Store is a required parameter.");
			return model;
		}

		// check to see if store exists
		// if it does not, an exception will get thrown
		AVMUtil.lookupStoreDNS(avmService, store);
		
		String stagingStore = AVMUtil.buildStagingStoreName(AVMUtil.getStoreId(store));
		NodeRef webProjectNodeRef = (NodeRef) avmService.getStoreProperty(stagingStore, 
	                                     SandboxConstants.PROP_WEB_PROJECT_NODE_REF).getValue(DataTypeDefinition.NODE_REF);
       
		if (webProjectNodeRef == null || !nodeService.exists(webProjectNodeRef)) {
			status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "Web project does not exist.");
			return model;			
		}
		
		// if this store is already allocated, use that, otherwise
		// try to find a new server available in the pool
		NodeRef serverRef = null;
		List<NodeRef> allocServerList = findDeployToServers(webProjectNodeRef, store);
		if (allocServerList.isEmpty()) {
			// nothing to do
			return model;
		} else {
			serverRef = allocServerList.get(0);
		}
		
		// if the node exists, we're good
        if (nodeService.exists(serverRef)) {
        	// get all properties of the target server
        	Map<QName, Serializable> serverProps = nodeService.getProperties(serverRef);
                 
        	String serverUri = AVMDeployWebsiteAction.calculateServerUri(serverProps);
        	String serverName = (String)serverProps.get(WCMAppModel.PROP_DEPLOYSERVERNAME);
        	if (serverName == null || serverName.length() == 0) {
        		serverName = serverUri;
        	}

        	// Allocate the test server to the current sandbox so it can re-use it and
        	// more importantly, no one else can. Before doing that however,
        	// we need to make sure no one else has taken the server since
        	// we selected it.

        	String allocatedTo = (String)serverProps.get(WCMAppModel.PROP_DEPLOYSERVERALLOCATEDTO);
        	if ((allocatedTo != null) && allocatedTo.equals(store)) {
        		nodeService.setProperty(serverRef, WCMAppModel.PROP_DEPLOYSERVERALLOCATEDTO, null);
        	}
                 
            // store the server deployed to in the model
            model.put("serverName", serverName);

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

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setImporterBootstrap(ImporterBootstrap importerBootstrap) {
		this.importerBootstrap = importerBootstrap;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}
