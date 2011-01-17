package com.someco.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.log4j.Logger;

import com.someco.model.SomeCoModel;

public class RatingServiceImpl implements RatingService {

	private NodeService nodeService;
	private SearchService searchService;
	
	private Logger logger = Logger.getLogger(RatingServiceImpl.class);

	public RatingData getRatingData(NodeRef nodeRef) {
		
		Integer total = (Integer)nodeService.getProperty(nodeRef, SomeCoModel.PROP_TOTAL_RATING);
		if (total == null) total = 0;
		Integer count = (Integer)nodeService.getProperty(nodeRef, SomeCoModel.PROP_RATING_COUNT);
		if (count == null) count = 0;
		Double rating = (Double)nodeService.getProperty(nodeRef, SomeCoModel.PROP_AVERAGE_RATING);
		if (rating == null) rating = 0.0d;
 
		RatingData ratingData = new RatingDataImpl(total, count, rating);

		return ratingData;
	}

	public int getUserRating(NodeRef nodeRef, String user) {
		int rating = 0;
		
		if (user == null || user.equals("")) {
			logger.debug("User name was not passed in");
			return rating;
		}

		String queryString = "PARENT:\"" + nodeRef.toString() + "\" AND @sc\\:rater:\"" + user + "\"";

		ResultSet results = searchService.query(nodeRef.getStoreRef(), SearchService.LANGUAGE_LUCENE, queryString);
		
		List<NodeRef> resultList = results.getNodeRefs();
		
		if (resultList == null || resultList.isEmpty()) {
			logger.debug("No ratings found for this node for user: " + user);
		} else {
			NodeRef ratingNodeRef = resultList.get(resultList.size()-1);
			Integer ratingProp = (Integer) nodeService.getProperty(ratingNodeRef, SomeCoModel.PROP_RATING);
			
			if (ratingProp != null) {
				rating = ratingProp;
			}
		}

		return rating;
	}

	public boolean hasRatings(NodeRef nodeRef) {
		List<NodeRef> ratingList = getRatings(nodeRef);
		return !ratingList.isEmpty();
	}
	
	public List<NodeRef> getRatings(NodeRef nodeRef) {
		ArrayList<NodeRef> returnList = new ArrayList<NodeRef>();
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef, (QNamePattern) SomeCoModel.ASSN_SC_RATINGS, new RegexQNamePattern(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "rating.*"));
		for (ChildAssociationRef child : children) {
			returnList.add(child.getChildRef());			
		}
		return returnList;
	}
	
	public void rate(NodeRef nodeRef, int rating, String user) {
		createRatingNode(nodeRef, rating, user);
	}
	
	protected void createRatingNode(final NodeRef nodeRef, final int rating, final String user) {

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
	
	public void deleteRatings(NodeRef nodeRef) {
		// check the parent to make sure it has the right aspect
		if (nodeService.hasAspect(nodeRef, SomeCoModel.ASPECT_SC_RATEABLE)) {
			// continue, this is what we want
		} else {
			logger.debug("Node did not have rateable aspect.");
			return;
		}
		
		// get the node's children
		List<NodeRef> ratingList = getRatings(nodeRef);

		if (ratingList.size() == 0) {
			// No children so no work to do
			if (logger.isDebugEnabled()) logger.debug("No children found");			
		} else {
			// iterate through the children and remove each one			
			for (NodeRef ratingNodeRef : ratingList) {
				nodeService.removeChild(nodeRef, ratingNodeRef);
			}
		}
	}
	
	public class RatingDataImpl implements RatingService.RatingData {
		protected int total;
		protected int count;
		protected double rating;
		
		public RatingDataImpl(int total, int count, double rating) {
			this.total = total;
			this.count = count;
			this.rating = rating;
		}
		
		public int getCount() {
			return count;
		}
		
		public double getRating() {
			return rating;
		}
		
		public int getTotal() {
			return total;
		}
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}
