package com.someco.examples;

import org.alfresco.webservice.accesscontrol.ACE;
import org.alfresco.webservice.accesscontrol.AccessStatus;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * This class sets the permissions on an object.
 */
public class SomeCoDataPermitter extends ExampleBase {
    private static final String USAGE = "java SomeCoDataPermitter <username> <password> <target uuid> <authority name> <permission>" +
                                        "\r\nExample: java SomeCoDataPermitter admin admin 65c3568e-5cfb-11dd-86b0-8d9521f1e84b \"Publisher\" \"PortalPublisher\"";
    
    private String targetUuid;
    private String authority;
    private String permission;
    
	public static void main(String[] args) throws Exception {
    	if (args.length != 5) doUsage(SomeCoDataPermitter.USAGE);
    	SomeCoDataPermitter scdp = new SomeCoDataPermitter();
    	scdp.setUser(args[0]);
    	scdp.setPassword(args[1]);
    	scdp.setTargetUuid(args[2]);
    	scdp.setAuthority(args[3]);
    	scdp.setPermission(args[4]);
    	scdp.update();
    }
    
    public void update() throws Exception {
        
    	// Start the session
        AuthenticationUtils.startSession(getUser(), getPassword());
        
        try {
        	// Create a reference to the document that will have its permissions set
            Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
            Reference doc = new Reference(storeRef, this.targetUuid, null);
       
            ACE ace = new ACE(this.authority, this.permission, AccessStatus.acepted);
                        
            WebServiceFactory.getAccessControlService().addACEs(new Predicate(new Reference[] {doc}, storeRef, null), new ACE[] {ace});     
            
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
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}

}
