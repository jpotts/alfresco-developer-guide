<#ftl ns_prefixes={"pr":"http://www.someco.com/corp/pr"}>
<#assign press_release = .vars["pr:press_release"]>
<div class="node">
  <h2>${press_release["pr:sub_title"]}</h2>
  <p><p>${press_release["pr:location"]} - ${press_release["pr:date"]} - ${press_release["pr:body"]}
			<#list press_release["pr:company_footer"] as cf>
				<p>${cf}</p>
			</#list>
			<#list press_release["pr:contact_info"] as ci>
				<p>${ci}</p>								
			</#list>
  </p></p>
  <div class="clearfix"></div>
</div>
<div class="clearfix"></div>