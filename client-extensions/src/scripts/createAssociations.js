var targetUuid="ba2dae61-ff4e-11dc-ba32-4d6fa120e858";

var queryResults = search.luceneSearch("+@sys\\:node-uuid:\"" + targetUuid + "\"");

// log matching web folders
if (queryResults.length > 0) {
  logger.log("Found " + queryResults.length + " matching nodes.");
  for (var i = 0; i < queryResults.length; i++) {
    logger.log("Creating association to:" + queryResults[i].name);                                                  
    document.createAssociation(queryResults[i], "sc:relatedDocuments");
    // you do not have to save the source document
  }
} else {
  logger.log("No matching nodes found.");
}