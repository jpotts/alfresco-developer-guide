package com.someco.util;

import java.util.Set;

import javax.faces.context.FacesContext;

import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.web.bean.repository.Repository;

public class GroupMembershipCheck {
	public static boolean isCurrentUserInGroup(FacesContext context, String groupName) {
		// The authority service returns authorities with their authority type
		// prefix prepended to the name, so we'll do that as well
		groupName = AuthorityType.GROUP.getPrefixString() + groupName;
		
		// get the current user
		String currentUserName = Repository.getServiceRegistry(context).getAuthenticationService().getCurrentUserName();
		
		// get the list of groups this user is contained by
		Set<String> authorityList = Repository.getServiceRegistry(context).getAuthorityService().getContainingAuthorities(AuthorityType.GROUP, currentUserName, false);
		
		// look in the list to see if groupName is in there. if so, return true;
		return authorityList.contains(groupName);
	}
}
