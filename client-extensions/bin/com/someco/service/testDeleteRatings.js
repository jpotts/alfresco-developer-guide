logger.log("Rated?" + ratings.hasRatings(document));

logger.log("Deleting ratings");
ratings.deleteRatings(document);

logger.log("Rated?" + ratings.hasRatings(document));