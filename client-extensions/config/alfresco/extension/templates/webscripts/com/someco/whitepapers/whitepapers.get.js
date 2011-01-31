//<import resource="classpath:alfresco/extension/scripts/rating.js">

var whitepapers = search.luceneSearch("+PATH:\"/app:company_home/cm:Someco/cm:Marketing/cm:Whitepapers/*\" +TYPE:\"sc:whitepaper\" +@sc\\:isActive:true");
logger.log("Got whitepapers");

if (whitepapers == null || whitepapers.length == 0) {
	logger.log("No whitepapers found");
	status.code = 404;
	status.message = "No whitepapers found";
	status.redirect = true;
} else {
	logger.log("User:" + args.user);
	var whitepaperInfo = new Array();
	for (i = 0; i < whitepapers.length; i++) {
		var ratingData = scRatings.getRatingData(whitepapers[i]);
		var rating = {};
		rating.average = ratingData.getRating();
		rating.count = ratingData.getCount();
		rating.user = scRatings.getUserRating(whitepapers[i], args.user);
	
		var whitepaper = new whitepaperEntry(whitepapers[i], rating);
		whitepaperInfo[i] = whitepaper;
	}
	model.whitepapers = whitepaperInfo;
}

function whitepaperEntry(whitepaper, rating) {
	this.whitepaper = whitepaper;
	this.rating = rating;
}