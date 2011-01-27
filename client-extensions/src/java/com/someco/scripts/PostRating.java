package com.someco.scripts;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.log4j.Logger;

import com.someco.model.SomeCoModel;
import com.someco.service.RatingService;

/**
 * This is the controller for the rating.post web script.
 * 
 * @author jpotts
 * 
 */
public class PostRating extends org.springframework.extensions.webscripts.DeclarativeWebScript {

	Logger logger = Logger.getLogger(PostRating.class);
			
	private NodeService nodeService;
	private RatingService ratingService;

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		int ratingValue = -1;
		String id =	req.getParameter("id");
		String rating = req.getParameter("rating");
		String user = req.getParameter("user");

		try {
			ratingValue = Integer.parseInt(rating);
		} catch (NumberFormatException nfe) {
		}
		
		if (id == null || rating == null ||	user == null) {
			logger.debug("ID, rating, or user not set");
			status.setCode(Status.STATUS_BAD_REQUEST, "Required data has not been provided");
		} else if ((ratingValue < 1) || (ratingValue > 5)) {
			logger.debug("Rating out of range");
			status.setCode(Status.STATUS_BAD_REQUEST, "Rating value must be between 1 and 5 inclusive");
		} else {
			logger.debug("Getting current node");
			NodeRef curNode = new NodeRef("workspace://SpacesStore/" + id);	
			if (!nodeService.exists(curNode)) {
				logger.debug("Node not found");
				status.setCode(Status.STATUS_NOT_FOUND, "No node found for id:" + id);
			} else {
				// Refactored to use the Rating Service
				//create(curNode, Integer.parseInt(rating), user);
				ratingService.rate(curNode, Integer.parseInt(rating), user);
			}
		
		}

		logger.debug("Setting model");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("node", id);
		model.put("rating", rating);
		model.put("user", user);
		
		return model;
	}
	
	/**
	 * 
	 * @param nodeRef
	 * @param rating
	 * @param user
	 * @deprecated Use the Rating Service instead
	 */
	protected void create(final NodeRef nodeRef, final int rating, final String user) {

		AuthenticationUtil.runAs(new RunAsWork<String>() {
			@SuppressWarnings("synthetic-access")
			public String doWork() throws Exception {
					// add the aspect to this document if it needs it		
					if (nodeService.hasAspect(nodeRef, SomeCoModel.ASPECT_SC_RATEABLE)) {
						logger.debug("Document already has aspect");
					} else {
						logger.debug("Adding rateable aspect");
						nodeService.addAspect(nodeRef, SomeCoModel.ASPECT_SC_RATEABLE, null);
					}
					
					Map<QName, Serializable> props = new HashMap<QName, Serializable>();
					props.put(SomeCoModel.PROP_RATING, rating);
					props.put(SomeCoModel.PROP_RATER, user);
			
					nodeService.createNode(
							nodeRef,
							SomeCoModel.ASSN_SC_RATINGS,
							QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_RATING.getLocalName() + new Date().getTime()),
							SomeCoModel.TYPE_SC_RATING,
							props);
			
					logger.debug("Created node");
					return "";
			}
		},
		"admin");					
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRatingService(RatingService ratingService) {
		this.ratingService = ratingService;
	}

}

