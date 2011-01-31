package com.someco.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.springframework.extensions.surf.util.Base64;
import org.alfresco.webservice.util.AuthenticationUtils;

public class SomeCoWebScriptInvoker {
	
	private static final String USAGE = "java SomeCoWebScriptInvoker <username> <password> <HTTP method> <web script URL>";
	
    private String user;
    private String password;
    private String scriptUrlString;
    private String method;
    private String ticket;
    
	public static void main(String[] args) throws Exception {
		if (args.length != 4) doUsage(SomeCoWebScriptInvoker.USAGE);
    	SomeCoWebScriptInvoker invoker = new SomeCoWebScriptInvoker();
    	invoker.setUser(args[0]);
    	invoker.setPassword(args[1]);
    	invoker.setMethod(args[2]);
    	invoker.setScriptUrl(args[3]);
    	invoker.doWork();
    }
	
	public void doWork() {
		HttpURLConnection conn = null;
		
		try {
			AuthenticationUtils.startSession(getUser(), getPassword());
			setTicket(AuthenticationUtils.getTicket());
			URL scriptUrl = (new URI(getScriptUrl())).toURL();
			
			System.out.println("Invoking: " + scriptUrl);
			
			conn = (HttpURLConnection)scriptUrl.openConnection();
			
			conn.setRequestMethod(getMethod());
			conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes((getTicket()).getBytes()));
			//3.0 and later can do HTTP method tunneling
			//conn.setRequestProperty("X-HTTP-Method-Override", "POST");
			conn.connect();			
						
			System.out.println("Response code: " + conn.getResponseCode());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));			
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			System.out.println(buffer.toString());
			
		} catch (Exception serviceException) {
	        throw new RuntimeException("Problem invoking web script", serviceException);
	    } finally {            
	        // End the session
	        AuthenticationUtils.endSession();
	        if (conn != null) conn.disconnect();
	    }
	}
	
	public static void doUsage(String message) {
		System.out.println(message);
		System.exit(0);
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getScriptUrl() {
		return scriptUrlString;
	}

	public void setScriptUrl(String scriptUrl) {
		this.scriptUrlString = scriptUrl;
	}
	
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
}
