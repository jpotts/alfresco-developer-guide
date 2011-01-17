package com.someco.examples;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

import com.someco.model.SomeCoModel;

/**
 * This class creates an sc:whitepaper object in the folder identified by the root folder parameter.
 */
public class SomeCoDataCreator extends ExampleBase {
    private static final String USAGE = "java SomeCoDataCreator <username> <password> <folder path> <sc content type> <content name>" +
                                        "\r\nExample: java SomeCoDataCreator admin admin /app:company_home/cm:someco/cm:marketing/cm:whitepapers whitepaper sample";
    
    private String contentType;
    private String contentName;

	public static void main(String[] args) throws Exception {
    	if (args.length != 5) doUsage(SomeCoDataCreator.USAGE);
    	SomeCoDataCreator scdc = new SomeCoDataCreator();
    	scdc.setUser(args[0]);
    	scdc.setPassword(args[1]);
    	scdc.setFolderPath(args[2]);
    	scdc.setContentType(args[3]);
    	scdc.setContentName(args[4]);
    	scdc.create();
    }
    
    public void create() throws Exception {
        String timeStamp = new Long(System.currentTimeMillis()).toString();
        
    	// Start the session
        AuthenticationUtils.startSession(getUser(), getPassword());
        
        try {
        	// Create a reference to the parent where we want to create content
            Store storeRef = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
            ParentReference docParent = new ParentReference(storeRef, null, getFolderPath(), Constants.ASSOC_CONTAINS, Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, getContentName() + timeStamp));
            
            // Create an array of NamedValue objects with the props we want to set
            NamedValue nameValue = Utils.createNamedValue(Constants.PROP_NAME, getContentName() + " (" + timeStamp + ")");
            NamedValue activeValue = Utils.createNamedValue(Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_IS_ACTIVE_STRING), "true");
            NamedValue publishDateValue = Utils.createNamedValue(Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_PUBLISHED_STRING), "2007-04-01T00:00:00.000-05:00");
            NamedValue clientValue = Utils.createNamedValue(Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_CLIENT_NAME_STRING), "Little Lebowski Urban Achievers");
            NamedValue[] contentProps = new NamedValue[] {nameValue, activeValue, publishDateValue, clientValue};

            // Construct CML statement to create test doc content node            
            CMLCreate createDoc = new CMLCreate("ref1", docParent, null, null, null, Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, getContentType()), contentProps);
	        
            // Construct CML statement to add webable and client-related aspects
            // The "ref1" string is a reference that tells Alfresco we're adding aspects to the doc created in the CMLCreate step
            CMLAddAspect addWebableAspectToDoc = new CMLAddAspect(Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.ASPECT_SC_WEBABLE_STRING), null, null, "ref1");
	        CMLAddAspect addClientRelatedAspectToDoc = new CMLAddAspect(Constants.createQNameString(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.ASPECT_SC_CLIENT_RELATED_STRING), null, null, "ref1");
	                    
            // Construct CML Block
            CML cml = new CML();
            cml.setCreate(new CMLCreate[] {createDoc});
            cml.setAddAspect(new CMLAddAspect[] {addWebableAspectToDoc, addClientRelatedAspectToDoc});
        
            // Execute CML Block
            UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);     
            Reference docRef = results[0].getDestination();
            dumpUpdateResults(results);

            // Nodes are created, now write some content
            ContentServiceSoapBindingStub contentService = WebServiceFactory.getContentService();
            ContentFormat contentFormat = new ContentFormat("text/plain", "UTF-8");
            String docText = "This is a sample " + getContentType() + " document called " + getContentName();
            Content docContentRef = contentService.write(docRef, Constants.PROP_CONTENT, docText.getBytes(), contentFormat);
            System.out.println("Content Length: " + docContentRef.getLength());
            
        } catch(Throwable e) {
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            // End the session
            AuthenticationUtils.endSession();
        }
    }
    
    public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
