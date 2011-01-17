package com.someco.scripts.remote;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.connector.remote.RemoteClient;
import org.alfresco.connector.remote.Response;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptStatus;

public class GetWhitepapers extends DeclarativeWebScript {

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, WebScriptStatus status) {
		Map<String, Object> model = new HashMap<String, Object>();
		
		RemoteClient remote = new RemoteClient("http://localhost:8080/alfresco/service");
		remote.setUsernamePassword(getUser(), getPassword());
		
		Response res = remote.call("/someco/whitepapers.json");

		// if the remote call is okay, set the response
		if (res.getStatus().getCode() == 200) {
			model.put("whitepapers", res.getResponse());
		} else {
			// otherwise, report the error
			status.setCode(res.getStatus().getCode());
			status.setMessage(res.getStatus().getMessage());
			status.setRedirect(true);
		}
		
		return model;
	}

	
	public String getUser() {
		return "admin";
	}
	
	public String getPassword() {
		return "admin";
	}
}
