var NUMBER_OF_STARS = 5;
function init_rating(url) {
    var ratings = document.getElementsByTagName('div');
    for (var i = 0; i < ratings.length; i++) {
        if (ratings[i].className != 'rating')
            continue;
            
        var rating = ratings[i].firstChild.nodeValue;
        ratings[i].removeChild(ratings[i].firstChild);
        if (rating > NUMBER_OF_STARS || rating < 0)
            continue;
        for (var j = 1; j <= NUMBER_OF_STARS; j++) {
            var star = document.createElement('img');
            if (rating >= 1) {
                star.setAttribute('src', 'someco/images/stars/rating_on.gif');
                star.className = 'on';
                rating--;
            } else if(rating > 0 && rating < 1) {
                star.setAttribute('src', 'someco/images/stars/rating_half.gif');
                star.className = 'half';
                rating = 0;
            } else {
                star.setAttribute('src', 'someco/images/stars/rating_off.gif');
                star.className = 'off';
            }
            
            var widgetId = ratings[i].getAttribute('id').substr(7);
            star.setAttribute('id', 'star_'+widgetId+'_'+j);
            star.setAttribute('onmouseover', "javascript:displayHover('"+widgetId+"', "+j+");");
            star.setAttribute('onmouseout', "javascript:displayNormal('"+widgetId+"', "+j+");");
            
            var rater = "jpotts";
            
            star.setAttribute('onclick', "javascript:postRating('" + widgetId + "', " + j + ", '" + rater + "', '" + url + "');");
            ratings[i].appendChild(star);
        } 
    }
}

function refresh_ratings(url) {
	var el = document.getElementById("whitepapers-menu");
	if (el.hasChildNodes()) {
		var guard = el.childNodes.length;
		for(var i=0; i<guard; i++) {
			el.removeChild(el.childNodes[0]);
		}
	}
	getWhitepapers(url);

}

function displayHover(ratingId, star) {
    for (var i = 1; i <= star; i++) {
        document.getElementById('star_'+ratingId+'_'+i).setAttribute('src', 'someco/images/stars/rating_over.gif');
    }
}

function displayNormal(ratingId, star) {
    for (var i = 1; i <= star; i++) {
        var status = document.getElementById('star_'+ratingId+'_'+i).className;
        document.getElementById('star_'+ratingId+'_'+i).setAttribute('src', 'someco/images/stars/rating_'+status+'.gif');
    }
}

function postRating(id, rating, rater, url) {

	var AjaxObject = {
	
		handleSuccess:function(o){
			alert("Posted rating");
			refresh_ratings("http://localhost:8080/alfresco/service/someco/whitepapers?guest=true");
		},
	
		handleFailure:function(o){
			// Failure handler
			alert("Failed to post rating");
		},
	
		startRequest:function(url) {
		   YAHOO.util.Connect.asyncRequest('POST', url, postcallback, "id=" + id + "&rating=" + rating + "&user=" + rater + "&guest=true");
		}
	};
	
	/*
	 * Define the callback object for success and failure
	 * handlers as well as object scope.
	 */
	var postcallback =
	{
		success:AjaxObject.handleSuccess,
		failure:AjaxObject.handleFailure,
		scope: AjaxObject
	};	
		
	AjaxObject.startRequest(url);	
}