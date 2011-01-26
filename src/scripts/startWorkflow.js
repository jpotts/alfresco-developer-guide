var workflow = actions.create("start-workflow");
workflow.parameters.workflowName = "jbpm$wf:adhoc";
workflow.parameters["bpm:workflowDescription"] = "Workflow from JavaScript";
workflow.parameters["bpm:assignee"] = "tuser1";
workflow.execute(document);
