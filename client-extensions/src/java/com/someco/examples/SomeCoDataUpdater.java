package com.someco.examples;

import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * This class updates a property on an object.
 */
public class SomeCoDataUpdater extends ExampleBase {
    private static final String USAGE = "java SomeCoDataUpdater <username> <password> <target uuid> <new name>" +
                                        "\r\nExample: java SomeCoDataUpdater admin admin 65c3568e-5cfb-11dd-86b0-8d9521f1e84b \"New Name\"";
    
    private String targetUuid;
    private String contentName;
    
	public static void main(String[] args) throws Exception {
    	if (args.length != 4) doUsage(SomeCoDataUpdater.USAGE);
    	SomeCoDataUpdater scdu = new SomeCoDataUpdater();
    	scdu.setUser(args[0]);
    	scdu.setPassword(args[1]);
    	scdu.setTargetUuid(args[2]);
    	scdu.setContentName(args[3]);
    	scdu.update();
    }
    
    public void update() throws Exception {
        
    	// Start the session
        AuthenticationUtils.startSession(getUser(), getPassword());
        
        try {
        	// Create a reference to the document that's getting updated
            Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
            Reference doc = new Reference(storeRef, this.targetUuid, null);
            
            // Create an array of NamedValue objects with the props we want to set
            NamedValue nameValue = Utils.createNamedValue(Constants.PROP_NAME, this.contentName);
            NamedValue[] contentProps = new NamedValue[] {nameValue};

            // Construct CML statement to update node            
            CMLUpdate updateDoc = new CMLUpdate(contentProps, new Predicate(new Reference[] {doc}, storeRef, null), null);
                    
            // Construct CML Block
            CML cml = new CML();
            cml.setUpdate(new CMLUpdate[] {updateDoc});
        
            // Execute CML Block
            UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);     
            dumpUpdateResults(results);

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
	
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
}
