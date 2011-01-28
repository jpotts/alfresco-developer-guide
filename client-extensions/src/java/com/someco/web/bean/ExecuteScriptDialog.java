package com.someco.web.bean;

import javax.faces.context.FacesContext;

import org.alfresco.repo.action.executer.ScriptActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.bean.dialog.BaseDialogBean;
import org.alfresco.web.bean.repository.Repository;

/**
 * Bean implementation for the "Execute Script Dialog"
 * 
 * @author gavinc
 */
public class ExecuteScriptDialog extends BaseDialogBean {
	
	private static final long serialVersionUID = 3154733996505637147L;

	protected String script;
   
	private ActionService actionService;
   
	@Override
	protected String finishImpl(FacesContext context, String outcome) throws Exception {
		// get the space the action will apply to
		NodeRef nodeRef = this.browseBean.getActionSpace().getNodeRef();
		
		// get the script the user selected in the dialog
	    NodeRef scriptRef = new NodeRef(Repository.getStoreRef(), getScript());
	    
		// use an action to execute the script
		Action action = this.actionService.createAction(ScriptActionExecuter.NAME);
		action.setParameterValue(ScriptActionExecuter.PARAM_SCRIPTREF, scriptRef);
		this.actionService.executeAction(action, nodeRef);
		      
		// return the default outcome
		return outcome;
	}
	
	@Override
	public boolean getFinishButtonDisabled() {
		if (getScript() != null && !getScript().equals("") && !getScript().equals("none")) {
			return false;
		} else {
			return true;
		}
	}
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}
}
