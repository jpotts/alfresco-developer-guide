package com.someco.bpm;

import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.workflow.jbpm.JBPMSpringActionHandler;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.apache.log4j.Logger;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.beans.factory.BeanFactory;

public class ExternalReviewNotification extends JBPMSpringActionHandler {
	private static final long serialVersionUID = 1L;

	private static final String FROM_ADDRESS = "alfresco@localhost";
	private static final String SUBJECT = "Workflow task requires action";
	private static final String RECIP_PROCESS_VARIABLE = "notificationRecipient";
	
	private static Logger logger = Logger.getLogger(ExternalReviewNotification.class);
	
	private ActionService actionService;
	
	@Override
	protected void initialiseHandler(BeanFactory factory) {
		actionService = (ActionService)factory.getBean("actionService");
	}

	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inside ExternalReviewNotification.execute()");
		String recipient = (String) executionContext.getVariable(ExternalReviewNotification.RECIP_PROCESS_VARIABLE);
		
		StringBuffer sb = new StringBuffer();
		sb.append("You have been assigned to a task named ");
		sb.append(executionContext.getToken().getNode().getName());
		sb.append(". Take the appropriate action by clicking one of the links below:\r\n\r\n");
		List<Transition> transitionList = executionContext.getNode().getLeavingTransitions();
		for (Iterator<Transition> it = transitionList.iterator(); it.hasNext(); ) {
			Transition transition = (Transition)it.next();
			sb.append(transition.getName());
			sb.append("\r\n");
			sb.append("http://localhost:8080/alfresco/service/someco/bpm/review?id=jbpm$");
			sb.append(executionContext.getProcessInstance().getId());
			sb.append("-@");
			sb.append("&action=");
			sb.append(transition.getName());
			sb.append("&guest=true");
			sb.append("\r\n\r\n");
		}
		
		logger.debug("Message body:" + sb.toString());
		
		Action mailAction = this.actionService.createAction(MailActionExecuter.NAME);
        mailAction.setParameterValue(MailActionExecuter.PARAM_SUBJECT, ExternalReviewNotification.SUBJECT);        
        mailAction.setParameterValue(MailActionExecuter.PARAM_TO, recipient);
        mailAction.setParameterValue(MailActionExecuter.PARAM_FROM, ExternalReviewNotification.FROM_ADDRESS);
        mailAction.setParameterValue(MailActionExecuter.PARAM_TEXT, sb.toString());
        
        this.actionService.executeAction(mailAction, null);
		
        logger.debug("Mail action executed");
        
		return;
	}

}
