package com.someco.avm;

import org.alfresco.service.cmr.avm.deploy.DeploymentCallback;
import org.alfresco.service.cmr.avm.deploy.DeploymentEvent;
import org.apache.log4j.Logger;

public class DeploymentLogger implements DeploymentCallback {

	private Logger logger = Logger.getLogger(DeploymentLogger.class);
	
	public void eventOccurred(DeploymentEvent event) {
		if (event.getType() == DeploymentEvent.Type.END) {
			if (logger.isDebugEnabled()) logger.debug("The deployment to " + event.getDestination() + " has ended.");
		}

	}

}
