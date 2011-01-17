package com.someco.action.handler;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;
import com.someco.action.executer.MoveReplacedActionExecuter;

public class MoveReplacedHandler extends BaseActionHandler {
	private static final long serialVersionUID = 1L;
	public final static String CUSTOM_ACTION_JSP = "/jsp/extension/actions/" + MoveReplacedActionExecuter.NAME + ".jsp";
	public String getJSPPath() {
		return CUSTOM_ACTION_JSP;
	}

   public void prepareForSave(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps) {
	   
      // add the destination space id to the action properties
      NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
      repoProps.put(MoveReplacedActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
      
      // add the type and name of the association to create when the move
      // is performed
      repoProps.put(MoveReplacedActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
            ContentModel.ASSOC_CONTAINS);
      repoProps.put(MoveReplacedActionExecuter.PARAM_ASSOC_QNAME, 
            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "move"));
      
   }

   public void prepareForEdit(Map<String, Serializable> actionProps, Map<String, Serializable> repoProps) {
	   
      NodeRef destNodeRef = (NodeRef)repoProps.get(MoveReplacedActionExecuter.PARAM_DESTINATION_FOLDER);
      actionProps.put(PROP_DESTINATION, destNodeRef);
      
   }

   public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> actionProps) {
      NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
      String spaceName = Repository.getNameForNode(
            Repository.getServiceRegistry(context).getNodeService(), space);
      
      return MessageFormat.format(Application.getMessage(context, "action_move_replaced"),
            new Object[] {spaceName});
   }
}
