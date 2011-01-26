<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
{"whitepapers" : [
<#list whitepapers as child>
        {"name" : "${child.whitepaper.properties.name}",
         "title" : "${child.whitepaper.properties["cm:title"]?j_string}",
         "link" : "${url.context}${child.whitepaper.url}",
         "icon" : "/alfresco/${child.whitepaper.icon16}",
         "type" : "${child.whitepaper.mimetype}",
         "size" : "${child.whitepaper.size}",
         "id" : "${child.whitepaper.id}",
         "description" : "<#if child.whitepaper.properties["cm:description"]?exists && child.whitepaper.properties["cm:description"] != "">${child.whitepaper.properties["cm:description"]?j_string}</#if>",
         "pubDate" : "${child.whitepaper.properties["cm:modified"]?string(datetimeformat)}",
         "rating" : {
	         "average" : "${child.rating.average}",
    	     "count" : "${child.rating.count}",
        	 "user" : "${child.rating.user}"
         }
        }
      <#if !(child.whitepaper == whitepapers?last.whitepaper)>,</#if>
</#list>
]
}