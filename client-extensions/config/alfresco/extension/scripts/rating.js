/**
	At the end of Chapter 6 a RatingService was introduced that is exposed as
	a root object in JavaScript which deprecates much of this file.
	
	This file should now only include logic needed when/if a JavaScript rating behavior
	is used instead of a Java-based behavior.
*/

/**
	This function is used by the JavaScript-based Rating behavior.
*/
function computeAverage(childAssocRef) {
		var parentRef = childAssocRef.parent;
		
		// check the parent to make sure it has the right aspect
		if (parentRef.hasAspect("{http://www.someco.com/model/content/1.0}rateable")) {
			// continue, this is what we want
		} else {
			logger.log("Rating's parent ref did not have rateable aspect.");
			return;
		}
		
		// get the parent node's children
		var children = parentRef.children;

		// iterate through the children to compute the total
		var average = 0.0;
		var total = 0;

		if (children != null && children.length > 0) {
			for (i in children) {
				var child = children[i];
				var rating = child.properties["{http://www.someco.com/model/content/1.0}rating"];
				total += rating;
			}
		
			// compute the average
			average = total / children.length;
		}			
	
		logger.log("Computed average:" + average);
		
		// store the average on the parent node
		parentRef.properties["{http://www.someco.com/model/content/1.0}averageRating"] = average;
		parentRef.properties["{http://www.someco.com/model/content/1.0}totalRating"] = total;
		parentRef.properties["{http://www.someco.com/model/content/1.0}ratingCount"] = children.length;
		parentRef.save();
		
		logger.log("Property set");
		
		return;

}

/**
	Use the rating service instead of this function
*/
/*
function getUserRating(curNode, curUser) {
	if (curUser == undefined || curUser == "") {
		logger.log("User name was not passed in");
		return 0;
	}
	
	var results = curNode.childrenByXPath("*//.[@sc:rater='" + curUser + "']");
	if (results == undefined || results.length == 0) {
		logger.log("No ratings found for this node for user: " + curUser);
		return 0;
	} else {
		var rating = results[results.length-1].properties["{http://www.someco.com/model/content/1.0}rating"];
		if (rating == undefined) {
			return 0;
		} else {
			return rating;
		}
	}
}
*/

/**
	This function has been refactored to leverage the Rating Service.
*/
/*
function getRating(curNode, curUser) {
	var rating = {};
*/
	/*
	rating.average = curNode.properties["{http://www.someco.com/model/content/1.0}averageRating"];
	rating.count = curNode.properties["{http://www.someco.com/model/content/1.0}ratingCount"];
	rating.user = getUserRating(curNode, curUser);
	*/
	// Use the factored RatingService instead
	/*
	var ratingData = scRatings.getRatingData(curNode);
	logger.log("User rating:" + 

	var ratingData = scRatings.getRatingData(curNode);

	rating.average = ratingData.getRating());
	rating.count = ratingData.getCount());
	rating.user = scRatings.getUserRating(curNode, curUser));
	return rating;
}
*/

/**
	Use the Rating Service instead of this function
*/
/*
function deleteRatings(curNode) {
		
		// check the parent to make sure it has the right aspect
		if (curNode.hasAspect("{http://www.someco.com/model/content/1.0}rateable")) {
			// continue, this is what we want
		} else {
			logger.log("Node did not have rateable aspect.");
			return;
		}
		
		// get the node's children
		var children = curNode.children;

		if (children != null && children.length > 0) {
			logger.log("Found children...iterating");
			for (i in children) {
				var child = children[i];
				logger.log("Removing child: " + child.id);
				child.remove();
			}
		}	
}
*/