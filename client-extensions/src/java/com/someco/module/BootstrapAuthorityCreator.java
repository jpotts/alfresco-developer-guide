package com.someco.module;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationContext;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

public class BootstrapAuthorityCreator extends AbstractLifecycleBean {
	private AuthorityService authorityService;
    private PersonService personService;
    private TransactionService transactionService;
    private MutableAuthenticationService authenticationService;
    private static Logger logger = Logger.getLogger(BootstrapAuthorityCreator.class);
    private AuthenticationContext authenticationContext;
    
    public void bootstrap() {
    	try
        {
            // import the content - note: in MT case, this will run in System context of tenant domain
            RunAsWork<Object> importRunAs = new RunAsWork<Object>()
            {
                public Object doWork() throws Exception
                {
                    RetryingTransactionCallback<Object> doImportCallback = new RetryingTransactionCallback<Object>()
                    {
                        public Object execute() throws Throwable
                        {   
                            doBootstrap();
                            return null;
                        }
                    };
                    return transactionService.getRetryingTransactionHelper().doInTransaction(doImportCallback, transactionService.isReadOnly(), false);
                }
            };
            AuthenticationUtil.runAs(importRunAs, authenticationContext.getSystemUserName());
        }
        catch(Throwable e)
        {
            throw new AlfrescoRuntimeException("Bootstrap failed", e);
        }
    }
    
    private void doBootstrap() {
    	String salesGroup;
    	String marketingGroup;

    	// create tuser1, tuser2, tuser3, tuser4
        if(!authenticationService.authenticationExists("tuser1")) {
    		authenticationService.createAuthentication("tuser1", "password".toCharArray());
    		if (logger.isDebugEnabled()) logger.debug("Created tuser1 auth");
        }

        if (!personService.personExists("tuser1")) {
    		personService.createPerson(createDefaultProperties("tuser1", "Test", "User1", "tuser1@localhost", "password"));
    		if (logger.isDebugEnabled()) logger.debug("Created tuser1 person");
    	}

        if(!authenticationService.authenticationExists("tuser2")) {
    		authenticationService.createAuthentication("tuser2", "password".toCharArray());
    		if (logger.isDebugEnabled()) logger.debug("Created tuser2 auth");
        }

    	if (!personService.personExists("tuser2")) {
    		personService.createPerson(createDefaultProperties("tuser2", "Test", "User2", "tuser2@localhost", "password"));
    		if (logger.isDebugEnabled()) logger.debug("Created tuser2 person");
    	}

        if(!authenticationService.authenticationExists("tuser3")) {
    		authenticationService.createAuthentication("tuser3", "password".toCharArray());
    		if (logger.isDebugEnabled()) logger.debug("Created tuser3 auth");
        }

    	if (!personService.personExists("tuser3")) {
    		personService.createPerson(createDefaultProperties("tuser3", "Test", "User3", "tuser3@localhost", "password"));
    		if (logger.isDebugEnabled()) logger.debug("Created tuser3 person");
    	}

        if(!authenticationService.authenticationExists("tuser4")) {
    		authenticationService.createAuthentication("tuser4", "password".toCharArray());
    		if (logger.isDebugEnabled()) logger.debug("Created tuser4 auth");
        }

    	if (!personService.personExists("tuser4")) {
    		personService.createPerson(createDefaultProperties("tuser4", "Test", "User4", "tuser4@localhost", "password"));
    		if (logger.isDebugEnabled()) logger.debug("Created tuser4 person");
    	}
    	
    	if (authorityService.authorityExists(authorityService.getName(AuthorityType.GROUP, "sales"))) {
    		salesGroup = authorityService.getName(AuthorityType.GROUP, "sales");
    	} else {
    		// create the sales group
    		salesGroup = authorityService.createAuthority(AuthorityType.GROUP, "sales");
    	}

    	//add tuser1 and tuser2 to the sales group
    	authorityService.addAuthority(salesGroup, "tuser1");
    	authorityService.addAuthority(salesGroup, "tuser2");
    	
    	if (authorityService.authorityExists(authorityService.getName(AuthorityType.GROUP, "marketing"))) {
    		marketingGroup = authorityService.getName(AuthorityType.GROUP, "marketing");
    	} else {
    		// create the marketing group
    		marketingGroup = authorityService.createAuthority(AuthorityType.GROUP, "marketing");
    	}

    	//add tuser3 and tuser4 to the marketing group
    	authorityService.addAuthority(marketingGroup, "tuser3");
    	authorityService.addAuthority(marketingGroup, "tuser4");
    }
    
    private Map<QName, Serializable> createDefaultProperties(String userName, String firstName, String lastName,
            String email, String password) {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USERNAME, userName);
        properties.put(ContentModel.PROP_FIRSTNAME, firstName);
        properties.put(ContentModel.PROP_LASTNAME, lastName);
        properties.put(ContentModel.PROP_EMAIL, email);
        properties.put(ContentModel.PROP_PASSWORD, password);
        return properties;
    }

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setAuthenticationService(MutableAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

    @Override
    protected void onBootstrap(ApplicationEvent event) {
        bootstrap();
    }
	
    public void setAuthenticationContext(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

	@Override
	protected void onShutdown(ApplicationEvent arg0) {
		// noop		
	}
	
	
}
