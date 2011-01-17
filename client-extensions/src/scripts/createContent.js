var contentType = "whitepaper";
var contentName = "sample-a";

// create a new whitepaper node as child of the current node and set its properties
var timestamp = new Date().getTime();
var whitepaperNode = space.createNode(contentName + timestamp, "sc:" + contentType);
whitepaperNode.addAspect("sc:webable");
whitepaperNode.properties["cm:name"] = contentName + " (" + timestamp + ")";
whitepaperNode.properties["sc:isActive"] = true;
whitepaperNode.properties["sc:published"] = new Date("04/01/2007");
whitepaperNode.content = "This is a sample " + contentType + " document called " + contentName;
whitepaperNode.save();