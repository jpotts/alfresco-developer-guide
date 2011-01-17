<#ftl ns_prefixes={"D", "http://www.someco.com/corp/pr"}>
<?xml version='1.0' encoding='UTF-8'?>
<rss version="2.0"
	xmlns:content="http://purl.org/rss/1.0/modules/content/"
	xmlns:wfw="http://wellformedweb.org/CommentAPI/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	>

	<channel>
		<title>someco.com</title>
		<link>http://localhost</link>
		<description>SomeCo Corporate Press Releases</description>
		<pubDate>${folder.parent.properties.modified?datetime}</pubDate>
	
		<generator>http://www.alfresco.com</generator>
		<language>en</language>
		<#list folder.children as child>
			<#assign pr_doc=child.xmlNodeModel>
			<item>
				<title>${pr_doc.press_release.title}</title>
				<link>http://localhost:8080${url.serviceContext}/api/node/content/${child.nodeRef.storeRef.protocol}/${child.nodeRef.storeRef.identifier}/${child.nodeRef.id}/${child.name?url}</link>
				<pubDate>${child.properties.modified?datetime}</pubDate>
				<dc:creator>${child.properties.creator}</dc:creator>
				<guid isPermaLink="false">${child.properties["sys:node-dbid"]?c}</guid>
				<description><![CDATA[${pr_doc.press_release.sub_title}]]></description>
				<content:encoded><![CDATA[${pr_doc.press_release.body}]]></content:encoded>
			</item>
		</#list>
	</channel>
</rss>