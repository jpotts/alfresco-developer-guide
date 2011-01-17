<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<script type="text/javascript">
function checkButtonState() {
	if (document.getElementById("dialog:dialog-body:script").value == "none" ) {
		document.getElementById("dialog:finish-button").disabled = true;
	} else {
		document.getElementById("dialog:finish-button").disabled = false;
	}
}
</script>

<%-- Scripts drop-down selector --%>
<h:outputText value="#{msg.select_a_script}: " />
<h:selectOneMenu id="script" value="#{DialogManager.bean.script}" onchange="checkButtonState();">
	<f:selectItems value="#{TemplateSupportBean.scriptFiles}" />
</h:selectOneMenu>