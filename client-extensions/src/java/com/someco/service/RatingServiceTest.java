package com.someco.service;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

import com.someco.model.SomeCoModel;
import com.someco.service.RatingService.RatingData;

public class RatingServiceTest extends TestCase {
	private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    private AuthenticationComponent authenticationComponent;
	private RatingService ratingService;
	private NodeService nodeService;
	
	public RatingServiceTest() {
    	super();
    }
    
    public void setUp() throws Exception {
            nodeService = (NodeService) ctx.getBean("NodeService");
            ratingService = (RatingService) ctx.getBean("RatingService");
            authenticationComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");

            this.authenticationComponent.setSystemUserAsCurrentUser();
            
    }
    
    @Override
    protected void tearDown() throws Exception {
        authenticationComponent.clearCurrentSecurityContext();
        super.tearDown();
    }
    
    /**
     * Test the fact that we can create a rating.
     */
    public void testCreate() throws Throwable {
    		StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_Create_" + System.currentTimeMillis());
    		NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
        
    		NodeRef testNode1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{test}01"),
                SomeCoModel.TYPE_SC_DOC).getChildRef();
    		
    		ratingService.rate(testNode1, 1, "user1");
    	
    		assertTrue(ratingService.hasRatings(testNode1));
       
    }
    
    public void testRatingData() {
		StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_Rating_Data_" + System.currentTimeMillis());
		NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
    
		NodeRef testNode1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{test}01"),
            SomeCoModel.TYPE_SC_DOC).getChildRef();
		
		ratingService.rate(testNode1, 1, "user1");
		ratingService.rate(testNode1, 2, "user2");
		ratingService.rate(testNode1, 3, "user3");		

    	// rating data shouldn't be null
    	RatingData ratingData = ratingService.getRatingData(testNode1);    	
    	assertNotNull(ratingData);
    	
    	// average rating should be 2.0
    	assertEquals(2.0d, ratingService.getRatingData(testNode1).getRating());   
    	// total of all ratings is 6
    	assertEquals(6, ratingService.getRatingData(testNode1).getTotal());
    	// number of ratings is 3
    	assertEquals(3, ratingService.getRatingData(testNode1).getCount());    	
    }
    
    public void testDelete() {
		StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_Delete_" + System.currentTimeMillis());
		NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
    
		NodeRef testNode1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{test}01"),
            SomeCoModel.TYPE_SC_DOC).getChildRef();
		
		ratingService.rate(testNode1, 1, "user1");
		ratingService.rate(testNode1, 2, "user2");
		ratingService.rate(testNode1, 3, "user3");		

    	assertTrue(ratingService.hasRatings(testNode1));
    	
    	ratingService.deleteRatings(testNode1);
    	
    	assertFalse(ratingService.hasRatings(testNode1));

    	assertEquals(0.0d, ratingService.getRatingData(testNode1).getRating());   
    	assertEquals(0, ratingService.getRatingData(testNode1).getTotal());
    	assertEquals(0, ratingService.getRatingData(testNode1).getCount());    	
    	
    }
       
    public void testUserRating() {
    	StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_User_Rating_" + System.currentTimeMillis());
		NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
    
		NodeRef testNode1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{test}01"),
            SomeCoModel.TYPE_SC_DOC).getChildRef();
		
		ratingService.rate(testNode1, 1, "user1");
		ratingService.rate(testNode1, 2, "user2");
		ratingService.rate(testNode1, 3, "user2");		

    	assertEquals(1, ratingService.getUserRating(testNode1, "user1"));
    	assertEquals(3, ratingService.getUserRating(testNode1, "user2"));
    	assertEquals(0, ratingService.getUserRating(testNode1, "userX"));
    }
}
