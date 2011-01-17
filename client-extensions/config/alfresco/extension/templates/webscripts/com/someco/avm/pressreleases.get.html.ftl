<html>
	<head>
		<title>Press releases in folder: ${folder.displayPath}/${folder.name}</title>
	</head>
	<body>
		<p><a href="${url.serviceContext}/sample/avm/stores">AVM Store</a>: ${store.id}</p>
		<p>AVM Folder: ${folder.displayPath}/${folder.name}</p>
		<table>
			<#list folder.children as child>
				<tr>
					<td>${child.properties.creator}</td>
					<td>${child.size}</td>
					<td>${child.properties.modified?datetime}</td>
					<td><a href="${url.serviceContext}/api/node/content/${child.nodeRef.storeRef.protocol}/${child.nodeRef.storeRef.identifier}/${child.nodeRef.id}/${child.name?url}">${child.name}</a>
				</tr>
			</#list>
		</table>
	</body>
</html>