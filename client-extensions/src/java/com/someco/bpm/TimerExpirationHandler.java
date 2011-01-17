package com.someco.bpm;

import java.util.Date;

import org.alfresco.repo.workflow.jbpm.JBPMSpringActionHandler;
import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.beans.factory.BeanFactory;

public class TimerExpirationHandler extends JBPMSpringActionHandler {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(TimerExpirationHandler.class);
	
	@Override
	protected void initialiseHandler(BeanFactory factory) {
		//noop
	}

	public void execute(ExecutionContext arg0) throws Exception {
		logger.debug("Inside TimerExpirationHandler timestamp: " + new Date());
	}

}
