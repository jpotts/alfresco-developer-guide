logger.log("Rated?" + ratings.hasRatings(document));

logger.log("Rating document three times");
ratings.rate(document, 1, person.properties.userName);
ratings.rate(document, 2, person.properties.userName);
ratings.rate(document, 3, person.properties.userName);

logger.log("Rated?" + ratings.hasRatings(document));