<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<h:panelGrid columns="2">
	<h:outputText value="Create discussion forum?" />
	<h:selectBooleanCheckbox title="discussionFlag" value="#{WizardManager.bean.discussionFlag}" />
	<h:outputText value="Discussion topic:" />
	<h:inputText id="topic" value="#{WizardManager.bean.discussionTopic}" size="50"/>
</h:panelGrid>