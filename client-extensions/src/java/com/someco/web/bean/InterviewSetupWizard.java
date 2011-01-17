package com.someco.web.bean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

import org.alfresco.model.ApplicationModel;
import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.search.impl.lucene.QueryParser;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.TemplateMailHelperBean;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.alfresco.web.ui.common.SortableSelectItem;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.Utils.URLMode;
import org.alfresco.web.ui.common.component.UIGenericPicker;
import org.springframework.mail.javamail.JavaMailSender;

import com.someco.model.SomeCoModel;

public class InterviewSetupWizard extends BaseWizardBean {
	
	private static final long serialVersionUID = -6630775170054628749L;
	
	// Constants
	private static final String MSG_USERS  = "users";
	private static final String MSG_GROUPS = "groups";
	private static final String MSG_ASSIGN = "assignment";
	private static final String MSG_OPTIONS = "options";
	private static final String MSG_NOTIFY = "notify";	
	private static final String MSG_INTERVIEWER = "interviewer";
	private static final String MSG_CANDIDATE_ROLE = "candidate_role";
	private static final String MSG_RESUME_LINK = "resume_link";
	private static final String MSG_LABEL_DISCUSSION = "label_discussion";
	private static final String MSG_LABEL_NO_DISCUSSION = "label_no_discussion";
	private static final String MSG_LABEL_NOTIFY = "label_notify";
	private static final String MSG_LABEL_NO_NOTIFY = "label_no_notify";
	private static final String MSG_DEFAULT_DISCUSSION_TOPIC = "default_discussion_topic";
	
	private static final String STEP_NOTIFY = "notify";
	private static final String NOTIFY_YES = "yes";
	private static final String NOTIFY_NO = "no";
		
	// Dependencies
	private AuthorityService authorityService;
	private PersonService personService;
	private PermissionService permissionService;
	private ContentService contentService;
	
	/** datamodel for table of users */
	transient private DataModel userDataModel = null;
	
	/** list of user/group role wrapper objects */
	protected List<UserGroup> userGroups = null;
	
	/** tracks whether or not a discussion forum should be established for this resume */
	private boolean discussionFlag = true;
	
	/** tracks whether or not a notification should be sent to the interviewers */
	private String notify = NOTIFY_NO;
	
	/** gets the discussion topic to use in the forum */
	private String discussionTopic = null;
	
	/** Helper providing template based mailing facilities */
	protected TemplateMailHelperBean mailHelper;
	
	/** JavaMailSender bean reference */
	transient private JavaMailSender mailSender;
	
	/**
	 * Initializes the wizard
	 */
	@Override
	public void init(Map<String, String> parameters) {
		super.init(parameters);

		notify = NOTIFY_NO;
		userDataModel = null;
		discussionFlag = true;
		discussionTopic = Application.getBundle(FacesContext.getCurrentInstance()).getString(MSG_DEFAULT_DISCUSSION_TOPIC);
		userGroups = new ArrayList<UserGroup>(5);
		mailHelper = new TemplateMailHelperBean();
		mailHelper.setMailSender(mailSender);
		mailHelper.setNodeService(getNodeService());
		
	}
	
	@Override
	protected String finishImpl(FacesContext context, String outcome)
			throws Exception {		
	      
		// get the Space to apply changes too
		NodeRef nodeRef = this.getNode().getNodeRef();

		if (NOTIFY_YES.equals(this.notify)) {
			sendNotification(context, nodeRef);
		}

		// if the discussion flag is set, create a topic
		if (isDiscussionFlag()) {
			createDiscussion(nodeRef, getDiscussionTopic());
		}
		
		return outcome;
	}
	
	@Override
	public String next() {
	      String stepName = Application.getWizardManager().getCurrentStepName();
	      
	      if (STEP_NOTIFY.equals(stepName)) {
	    	  buildDefaultNotification();
	      }
	      
	      return null;
	}
	
	/**
	 * This method builds the default notification subject and body.
	 */
	public void buildDefaultNotification() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        // prepare automatic text for email and display
        StringBuilder buf = new StringBuilder(256);
        
        String personName = Application.getCurrentUser(context).getFullName(this.getNodeService());
        String msgInterviewer = Application.getMessage(context, MSG_INTERVIEWER);
        String msgRole = Application.getMessage(context, MSG_CANDIDATE_ROLE);
        String msgResumeLink = Application.getMessage(context, MSG_RESUME_LINK);

        String candidateName = (String) this.getNodeService().getProperty(this.getNode().getNodeRef(), SomeCoModel.PROP_CANDIDATE_NAME);
        String candidateRole = (String) this.getNodeService().getProperty(this.getNode().getNodeRef(), SomeCoModel.PROP_CANDIDATE_ROLE);	         
        String downloadURL = getDownloadURL();
        
        buf.append(MessageFormat.format(msgInterviewer, new Object[] {personName, candidateName}));
        
        // default the subject line to an informative message
        this.mailHelper.setSubject(buf.toString());
    
        // add the role the candidate is interviewing for
        buf.append("\r\n\r\n");	         
        buf.append(MessageFormat.format(msgRole, new Object[] {candidateRole}));

        // provide a link to download the resume
        buf.append("\r\n\r\n");	         
        buf.append(MessageFormat.format(msgResumeLink, new Object[] {downloadURL}));
        
        // set the body content and default text to this text
        this.mailHelper.setAutomaticText(buf.toString());
        this.mailHelper.setBody(this.mailHelper.getAutomaticText());

	}

	/**
	 * This method sends the notification to all selected interviewers.
	 * @param context
	 * @param nodeRef
	 */
	public void sendNotification(FacesContext context, NodeRef nodeRef) {
		User user = Application.getCurrentUser(context);
		String from = (String)this.getNodeService().getProperty(user.getPerson(), ContentModel.PROP_EMAIL);
		if (from == null || from.length() == 0) {
			// if the user does not have an email address get the default one from the config service
			from = Application.getClientConfig(context).getFromEmailAddress();
		}
		
		// for each user send an email
		for (int i=0; i<this.userGroups.size(); i++) {
			UserGroup userGroup = this.userGroups.get(i);
			String authority = userGroup.getAuthority();

			// if User, email then, else if Group get all members and email them
			AuthorityType authType = AuthorityType.getAuthorityType(authority);
			if (authType.equals(AuthorityType.USER)) {
				if (this.getPersonService().personExists(authority) == true) {
					this.mailHelper.notifyUser(this.getPersonService().getPerson(authority), nodeRef, from, "");
				}
			} else if (authType.equals(AuthorityType.GROUP)) {
				// else notify all members of the group
				Set<String> users = this.getAuthorityService().getContainedAuthorities(AuthorityType.USER, authority, false);
				for (String userAuth : users) {
					if (this.getPersonService().personExists(userAuth) == true) {
						this.mailHelper.notifyUser(this.getPersonService().getPerson(userAuth), nodeRef, from, "");
					}
				} // next userAuth
			} // end if
			
		} // next i

	}
	
	/**
	 * This method creates a discussion forum for the specified node, if it doesn't exist already,
	 * then adds a new topic with the specified string if the topic does not yet exist.
	 * @param nodeRef
	 * @param topic
	 */
	public void createDiscussion(NodeRef nodeRef, String topic) {
		// if the discussable aspect is already there
		if (this.getNodeService().hasAspect(nodeRef, ForumModel.ASPECT_DISCUSSABLE)) {
			// do nothing
        } else {        
        	// otherwise, add the discussable aspect
        	this.getNodeService().addAspect(nodeRef, ForumModel.ASPECT_DISCUSSABLE, null);
        }
		
		// check for an existing discussion child node
        List<ChildAssociationRef> childRefs = this.getNodeService().getChildAssocs(nodeRef, ForumModel.ASSOC_DISCUSSION, QName.createQName(NamespaceService.FORUMS_MODEL_1_0_URI, "discussion"));
        ChildAssociationRef childRef;
        
        // if there isn't one
        NodeRef forumNodeRef;
		if (childRefs.size() == 0) {
	        // add the association
	        String name = (String)this.getNodeService().getProperty(nodeRef, ContentModel.PROP_NAME);
	        String msg = Application.getMessage(FacesContext.getCurrentInstance(), "discussion_for");
	        String forumName = MessageFormat.format(msg, new Object[] {name});
	        
	        // create forum
	        Map<QName, Serializable> forumProps = new HashMap<QName, Serializable>(1);
	        forumProps.put(ContentModel.PROP_NAME, forumName);
	        childRef = this.getNodeService().createNode(nodeRef, 
	              ForumModel.ASSOC_DISCUSSION,
	              QName.createQName(NamespaceService.FORUMS_MODEL_1_0_URI, "discussion"), 
	              ForumModel.TYPE_FORUM, forumProps);
	        
	        forumNodeRef = childRef.getChildRef();
	        
	        // apply the uifacets aspect
	        Map<QName, Serializable> uiFacetsProps = new HashMap<QName, Serializable>(5);
	        uiFacetsProps.put(ApplicationModel.PROP_ICON, "forum");
	        this.getNodeService().addAspect(forumNodeRef, ApplicationModel.ASPECT_UIFACETS, uiFacetsProps);
		} else {
			// otherwise, grab the first one
			childRef = childRefs.get(0);
			forumNodeRef = childRef.getChildRef();
		}
       
        // check to see if this topic has already been added
        if (this.getNodeService().getChildByName(forumNodeRef, ContentModel.ASSOC_CONTAINS, topic) == null) {
        	// create topic        
	        Map<QName, Serializable> topicProps = new HashMap<QName, Serializable>(1);
	        topicProps.put(ContentModel.PROP_NAME, topic);
	        ChildAssociationRef topicChildRef = this.getNodeService().createNode(forumNodeRef, 
	              ContentModel.ASSOC_CONTAINS,
	              QName.createQName(NamespaceService.FORUMS_MODEL_1_0_URI, "topic"), 
	              ForumModel.TYPE_TOPIC, topicProps);
	        
	        NodeRef topicNodeRef = topicChildRef.getChildRef();
	        
	        // apply the uifacets aspect
	        Map<QName, Serializable> uiFacetsProps = new HashMap<QName, Serializable>(5);
	        uiFacetsProps.put(ApplicationModel.PROP_ICON, "topic");
	        this.getNodeService().addAspect(topicNodeRef, ApplicationModel.ASPECT_UIFACETS, uiFacetsProps);        	
        } else {
        	// do nothing, the topic is already there
        }
       
	}
	
	/**
	 * This method renders the summary table shown on the summary step.
	 * @return Returns the summary data for the wizard.
	 */
	public String getSummary() {

	   ResourceBundle bundle = Application.getBundle(FacesContext.getCurrentInstance());

	   List<String> labels = new ArrayList<String>();
	   List<String> values = new ArrayList<String>();
	   
	   if (this.userGroups != null) {
		   labels.add(bundle.getString(MSG_ASSIGN));
		   StringBuffer buf = new StringBuffer();
		   for (Iterator<UserGroup> userGroupsIter = this.userGroups.iterator(); userGroupsIter.hasNext();) {
			   buf.append(userGroupsIter.next().getLabel());
			   if (userGroupsIter.hasNext()) buf.append(", ");
		   }
		   values.add(buf.toString());		   
	   }
	   
	   labels.add(bundle.getString(MSG_OPTIONS));
	   values.add(this.isDiscussionFlag() ? bundle.getString(MSG_LABEL_DISCUSSION) + this.getDiscussionTopic() : bundle.getString(MSG_LABEL_NO_DISCUSSION));
	   
	   labels.add(bundle.getString(MSG_NOTIFY));
	   values.add(this.notify.equals(NOTIFY_YES) ? bundle.getString(MSG_LABEL_NOTIFY) : bundle.getString(MSG_LABEL_NO_NOTIFY));

	   String[] labelArray = new String[labels.size()];
	   labels.toArray(labelArray);
	   
	   String[] valueArray = new String[values.size()];
	   values.toArray(valueArray);
	   
	   return buildSummary(labelArray, valueArray);
	   
	}
	
	/**
	 * Grabs the node for the wizard's context
	 * @return
	 */
	protected Node getNode() {
		return this.browseBean.getActionSpace();
	}
	
	/**
	 * @return String the full URL for a piece of content.
	 */
	public String getDownloadURL() {
		String downloadURL = Utils.generateURL(FacesContext.getCurrentInstance(), this.getNode(), URLMode.HTTP_DOWNLOAD); 
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        String servletUrl = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString();
        String hostname = servletUrl.substring(0, servletUrl.indexOf('/', 8));

        StringBuffer buf = new StringBuffer();
        buf.append(hostname);
        buf.append(contextPath);
        buf.append(downloadURL);
        
		return buf.toString();
	}

	/************************************************************************
	 * Methods borrowed from BaseInviteUsersWizard and modified for our needs
	 ************************************************************************/
	
	/**
	 * Property accessed by the Generic Picker component.
	 * 
	 * @return the array of filter options to show in the users/groups picker
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#getFilters()
	 */
	public SelectItem[] getFilters() {
		ResourceBundle bundle = Application.getBundle(FacesContext.getCurrentInstance());
	      
		return new SelectItem[] {
	            new SelectItem("0", bundle.getString(MSG_USERS)),
	            new SelectItem("1", bundle.getString(MSG_GROUPS)) };
	}
	
	/**
	 * Query callback method executed by the Generic Picker component.
	 * This method is part of the contract to the Generic Picker, it is up to the backing bean
	 * to execute whatever query is appropriate and return the results.
	 * 
	 * @param filterIndex        Index of the filter drop-down selection
	 * @param contains           Text from the contains textbox
	 * 
	 * @return An array of SelectItem objects containing the results to display in the picker.
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#pickerCallback(int filterIndex, String contains)
	 */
	public SelectItem[] pickerCallback(int filterIndex, String contains) {
		FacesContext context = FacesContext.getCurrentInstance();
	      
		SelectItem[] items;
	      
		UserTransaction tx = null;
		try {
	         tx = Repository.getUserTransaction(context, true);
	         tx.begin();
	         
	         List<SelectItem> results = new ArrayList<SelectItem>();
	         
	         if (filterIndex == 0) {
	            // Use lucene search to retrieve user details
	            String term = QueryParser.escape(contains.trim());
	            StringBuilder query = new StringBuilder(128);
	            query.append("@").append(NamespaceService.CONTENT_MODEL_PREFIX).append("\\:firstName:\"*");
	            query.append(term);
	            query.append("*\" @").append(NamespaceService.CONTENT_MODEL_PREFIX).append("\\:lastName:\"*");
	            query.append(term);
	            query.append("*\" @").append(NamespaceService.CONTENT_MODEL_PREFIX).append("\\:userName:");
	            query.append(term);
	            query.append("*");
	            ResultSet resultSet = Repository.getServiceRegistry(context).getSearchService().query(
	                    Repository.getStoreRef(),
	                    SearchService.LANGUAGE_LUCENE,
	                    query.toString());            
	            List<NodeRef> nodes = resultSet.getNodeRefs();            

	            for (int index=0; index<nodes.size(); index++) {
	               NodeRef personRef = nodes.get(index);
	               String firstName = (String)this.getNodeService().getProperty(personRef, ContentModel.PROP_FIRSTNAME);
	               String lastName = (String)this.getNodeService().getProperty(personRef, ContentModel.PROP_LASTNAME);
	               String username = (String)this.getNodeService().getProperty(personRef, ContentModel.PROP_USERNAME);
	               if (username != null) {
	                  SelectItem item = new SortableSelectItem(username, firstName + " " + lastName + " [" + username + "]", lastName);
	                  results.add(item);
	               }
	            }
	         } else {
	            // groups - simple text based match on name
	            Set<String> groups = getAuthorityService().getAllAuthorities(AuthorityType.GROUP);
	            groups.addAll(getAuthorityService().getAllAuthorities(AuthorityType.EVERYONE));

	            String containsLower = contains.trim().toLowerCase();
	            int offset = PermissionService.GROUP_PREFIX.length();
	            for (String group : groups) {
	               if (group.toLowerCase().indexOf(containsLower, offset) != -1) {
	                  results.add(new SortableSelectItem(group, group.substring(offset), group));
	               }
	            }
	         }
	         
	         items = new SelectItem[results.size()];
	         results.toArray(items);
	         Arrays.sort(items);
	         
	         // commit the transaction
	         tx.commit();
		} catch (Throwable err) {
	         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
	               FacesContext.getCurrentInstance(), Repository.ERROR_GENERIC), err.getMessage()), err );
	         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
	         
	         items = new SelectItem[0];
	    }
	      
	    return items;
	}

	/**
	 * Action handler called when the Add button is pressed to process the current selection
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#addSelect(ActionEvent event) 
	 */
	public void addSelection(ActionEvent event) {
		UIGenericPicker picker = (UIGenericPicker)event.getComponent().findComponent("picker");
	      
		String[] results = picker.getSelectedResults();
		if (results != null) {
            for (int i=0; i<results.length; i++) {
               addAuthority(results[i]);
            }

		}
	}
	
	/**
	 * Action handler called when the Remove button is pressed to remove a user
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#removeSelection(ActionEvent event) 
	 */
	public void removeSelection(ActionEvent event) {
		UserGroup wrapper = (UserGroup)getUserDataModel().getRowData();
		if (wrapper != null) {
	         this.userGroups.remove(wrapper);
		}
	}
	
	/**
	 * Add an authority to the list managed by this wizard.
	 * 
	 * @param authority        Authority to add (cannot be null)
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#addAuthorityWithRole(String, String)
	 */
	public void addAuthority(String authority) {
	      // only add if authority not already present in the list
	      boolean foundExisting = false;
	      for (int n=0; n<this.userGroups.size(); n++) {
	         UserGroup wrapper = this.userGroups.get(n);
	         if (authority.equals(wrapper.getAuthority())) {
	            foundExisting = true;
	            break;
	         }
	      }
	      
	      if (foundExisting == false) {
	         StringBuilder label = new StringBuilder(64);
	         
	         // build a display label showing the user and their role for the space
	         AuthorityType authType = AuthorityType.getAuthorityType(authority);
	         if (authType == AuthorityType.GUEST || authType == AuthorityType.USER) {
	            if (authType == AuthorityType.GUEST || getPersonService().personExists(authority) == true) {
	               // found a User authority
	               label.append(buildLabelForUserAuthority(authority));
	            }
	         } else {
	            // found a group authority
	            label.append(buildLabelForGroupAuthority(authority));
	         }
	         
	         this.userGroups.add(new UserGroup(authority, label.toString()));
	      }
	   }	
	
	/**
	 * Helper to build a label of the form:
	 *    Firstname Lastname (Role)
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#buildLabelForUserAuthorityRole(String, String)
	 */
	public String buildLabelForUserAuthority(String authority) {
		// found a User authority
		NodeRef ref = this.getPersonService().getPerson(authority);
		String firstName = (String)this.getNodeService().getProperty(ref, ContentModel.PROP_FIRSTNAME);
		String lastName = (String)this.getNodeService().getProperty(ref, ContentModel.PROP_LASTNAME);
	      
		StringBuilder buf = new StringBuilder(100);
		buf.append(firstName)
	         .append(" ")
	         .append(lastName != null ? lastName : "");
	      
		return buf.toString();
	}
	
	/**   
	 * Helper to build a label for a Group authority of the form:
	 *    Groupname (role)
	 * @param authority
	 * @return
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#buildLabelForGroupAuthorityRole(String, String)
	 */
	public String buildLabelForGroupAuthority(String authority) {
		StringBuilder buf = new StringBuilder(100);
		buf.append(authority.substring(PermissionService.GROUP_PREFIX.length()));
	      
		return buf.toString();
	}
	   
	/**
	 * Returns the properties for current user-roles JSF DataModel
	 * 
	 * @return JSF DataModel representing the current user-roles
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard#getUserRolesDataModel()
	 */
	public DataModel getUserDataModel() {
		if (this.userDataModel == null) {
	         this.userDataModel = new ListDataModel();
		}
	      
		// only set the wrapped data once otherwise the rowindex is reset
		if (this.userDataModel.getWrappedData() == null) {
	         this.userDataModel.setWrappedData(this.userGroups);
		}
	      
		return this.userDataModel;
	}	
	   
	/**
	 * Simple wrapper class to represent a user/group combination
	 * @see org.alfresco.web.bean.wizard.BaseInviteUsersWizard.UserGroupRole
	 */
	public static class UserGroup implements Serializable {

		private static final long serialVersionUID = 1462580744353149651L;

		public UserGroup(String authority, String label) {
	         this.authority = authority;
	         this.label = label;
		}
	      
		public String getAuthority() {
			return this.authority;
		}
	      
		public String getLabel() {
			return this.label;
		}
	      
		private String authority;
		private String label;
	}

	/******************************************************
	 * GETTERS AND SETTERS
	 ******************************************************/
	
	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public boolean isDiscussionFlag() {
		return discussionFlag;
	}

	public void setDiscussionFlag(boolean discussionFlag) {
		this.discussionFlag = discussionFlag;
	}

	public String getDiscussionTopic() {
		return discussionTopic;
	}

	public void setDiscussionTopic(String discussionTopic) {
		this.discussionTopic = discussionTopic;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}	
	   
	public String getNotify() {
		return this.notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public TemplateMailHelperBean getMailHelper() {
		return this.mailHelper;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
}
