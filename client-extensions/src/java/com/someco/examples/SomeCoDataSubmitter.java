package com.someco.examples;

import org.alfresco.webservice.action.Action;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * This class submits an object into a workflow.
 */
public class SomeCoDataSubmitter extends ExampleBase {
    private static final String USAGE = "java SomeCoDataSubmitter <username> <password> <target uuid> <workflow name> <assignee>" +
                                        "\r\nExample: java SomeCoDataSubmitter admin admin 65c3568e-5cfb-11dd-86b0-8d9521f1e84b \"jbpm$wf:adhoc\" \"tuser1\"";
    
    private String targetUuid;
    private String workflow;
    private String assignee;
    
	public static void main(String[] args) throws Exception {
    	if (args.length != 5) doUsage(SomeCoDataSubmitter.USAGE);
    	SomeCoDataSubmitter scds = new SomeCoDataSubmitter();
    	scds.setUser(args[0]);
    	scds.setPassword(args[1]);
    	scds.setTargetUuid(args[2]);
    	scds.setWorkflow(args[3]);
    	scds.setAssignee(args[4]);
    	scds.update();
    }
    
    public void update() throws Exception {
        
    	// Start the session
        AuthenticationUtils.startSession(getUser(), getPassword());
        
        try {
        	// Create a reference to the doc that will be submitted into the workflow
            Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
            Reference doc = new Reference(storeRef, this.targetUuid, null);
            
            NamedValue workflowValue = Utils.createNamedValue("workflowName", this.workflow);
            NamedValue descriptionValue = Utils.createNamedValue("bpm:workflowDescription", "Submitted from web service");
            NamedValue assigneeValue = Utils.createNamedValue("bpm:assignee", this.assignee);
            NamedValue[] actionArguments = new NamedValue[] {workflowValue, descriptionValue, assigneeValue};
            
            Action startWorkflowAction = new Action(null, this.targetUuid, "start-workflow", null, null, actionArguments, null, null, null);
            
            WebServiceFactory.getActionService().executeActions(new Predicate(new Reference[] {doc}, storeRef, null), new Action[] {startWorkflowAction});
            
        } catch(Throwable e) {
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            // End the session
            AuthenticationUtils.endSession();
        }
    }

	public void setTargetUuid(String targetUuid) {
		this.targetUuid = targetUuid;
	}
	
	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}
	
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

}
