logger.log("User rating:" + ratings.getUserRating(document, person.properties.userName));

var ratingData = ratings.getRatingData(document);

logger.log("Average:" + ratingData.getRating());
logger.log("Count:" + ratingData.getCount());
logger.log("Total:" + ratingData.getTotal());