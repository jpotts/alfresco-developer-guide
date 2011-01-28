package com.someco.action.executer;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.apache.log4j.Logger;

import com.someco.behavior.Rating;


public class MoveReplacedActionExecuter extends ActionExecuterAbstractBase {
    public static final String NAME = "move-replaced";
    public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";

    private Logger logger = Logger.getLogger(Rating.class);    
    
    /**
     * Node service
     */
    private NodeService nodeService;
	
	public void setNodeService(NodeService nodeService)	{
		this.nodeService = nodeService;
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_TYPE_QNAME, DataTypeDefinition.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_TYPE_QNAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_QNAME, DataTypeDefinition.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_QNAME)));
	}

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuter#execute(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void executeImpl(Action ruleAction, NodeRef actionedUponNodeRef) {
    	if (logger.isDebugEnabled()) logger.debug("Inside MoveReplaced.executeImpl");
    	
    	// get the replaces associations for this node
        //List<AssociationRef> assocRefs = nodeService.getTargetAssocs(actionedUponNodeRef, RegexQNamePattern.MATCH_ALL);
    	List<AssociationRef> assocRefs = nodeService.getTargetAssocs(actionedUponNodeRef, ((QNamePattern) QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "replaces")) );

    	// if there are none, return
        if (assocRefs.isEmpty()) {
        	// no work to do, return
        	if (logger.isDebugEnabled()) logger.debug("No associations so bailing");
        	return;
        } else {
        	if (logger.isDebugEnabled()) logger.debug("Got at least one assocation");
        	for (AssociationRef assocNode : assocRefs) {
		    	// create a noderef for the replaces association
        		NodeRef assocRef = assocNode.getTargetRef();

		    	// if the node exists
				if (this.nodeService.exists(assocRef)) {
					if (logger.isDebugEnabled()) logger.debug("Associated content node exists, preparing to move");
			        NodeRef destinationParent = (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
			        QName destinationAssocTypeQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
			        
			        String currentNameString = (String) this.nodeService.getProperty(assocRef, ContentModel.PROP_NAME);
			        
			        this.nodeService.moveNode(
			                assocRef,
			                destinationParent,
			                destinationAssocTypeQName,
			                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, currentNameString));
			        if (logger.isDebugEnabled()) logger.debug("Moved");
				}
        	} // next assocNode			
        } // end if isEmpty
    }

}