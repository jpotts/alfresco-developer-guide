package com.someco.scripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.Status;
import org.apache.log4j.Logger;

/**
 * This is the controller for the review.get web script.
 * 
 * @author jpotts 
 */
public class GetReview extends org.alfresco.web.scripts.DeclarativeWebScript {

	Logger logger = Logger.getLogger(GetReview.class);
			
	// Dependencies
	private WorkflowService workflowService;

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		
		final String id = req.getParameter("id");
		final String action = req.getParameter("action");

		if (id == null || action == null) {
			logger.debug("Email, ID, action, or secret not set");
			status.setCode(400);			
			status.setMessage("Required data has not been provided");
			status.setRedirect(true);
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("response", AuthenticationUtil.runAs(new RunAsWork<String>() {
			@SuppressWarnings("synthetic-access")
			public String doWork() throws Exception {
				
				logger.debug("About to signal id:" + id + " with transition:" + action);
				
				workflowService.signal(id, action);
				logger.debug("Signal sent.");	

				return "Success";
			}
		},
		"admin"));
	
		return model;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
}
