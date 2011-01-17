package com.someco.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.avm.AVMNodeConverter;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.Pair;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.log4j.Logger;

public class GetPressReleases extends org.alfresco.web.scripts.DeclarativeWebScript {

	Logger logger = Logger.getLogger(GetPressReleases.class);
	private SearchService searchService;
	private AVMService avmService;

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String project = req.getParameter("project");
		ResultSet results = null;
		ArrayList<AVMNodeDescriptor> pressreleasePaths = new ArrayList<AVMNodeDescriptor>();
		
		if (project == null) {
			logger.debug("project not set");
			status.setCode(400);			
			status.setMessage("Required data has not been provided");
			status.setRedirect(true);
		} else {
			String query = "+@wca\\:parentformname:\"press-release\"+ASPECT:\"{http://www.alfresco.org/model/wcmappmodel/1.0}rendition\"";
			StoreRef projectStore = new StoreRef(StoreRef.PROTOCOL_AVM, project);
	        SearchParameters sp = new SearchParameters();
	        sp.addStore(projectStore);
	        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
	        sp.setQuery(query);
	        sp.addSort("CreateDate", true);
	        try
	        {
	        	results = searchService.query(projectStore, "lucene", query);
	        }
	        finally
	        {
	           if(results != null)
	           {
	              results.close();
	              for(NodeRef node : results.getNodeRefs()) {
	            	  Pair<Integer,String> avmPair = AVMNodeConverter.ToAVMVersionPath(node);
	            	  pressreleasePaths.add(avmService.lookup((Integer)avmPair.getFirst(), (String)avmPair.getSecond()));
	              }
	           }
	        }
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("pressreleasePaths", pressreleasePaths.toArray());
		model.put("test", "test");

		return model;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public void setAvmService(AVMService avmService) {
		this.avmService = avmService;
	}
}