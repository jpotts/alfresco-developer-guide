<html>
	<head>
		<link type="text/css" href="css/style.css" rel="stylesheet">
		<script src="${url.context}/someco/javascript/prototype.js" type="text/javascript"></script>
		<script src="${url.context}/someco/javascript/rating-script.js" type="text/javascript"></script>
		<div id="result"></div>
	</head>
	<body>
		<script type="text/javascript">
			function initEvents() {
				init_rating();

			    $$('.rating').each(function(n){
			    n.immediateDescendants().each(function(c){
			        Event.observe(c, 'click', submitRating);
			        });
			    });
			}
			function submitRating(evt) {
                var tmp = Event.element(evt).getAttribute('id').substr(5);
                alert(tmp);
                var widgetId = tmp.substr(0, tmp.indexOf('_'));
                var starNbr = tmp.substr(tmp.indexOf('_')+1);
                alert("Post to URL:" + widgetId + "," + starNbr);
				if (document.login.userId.value != undefined && document.login.userId.value != "") {
					curUser = document.login.userId.value;
				} else {
					curUser = "jpotts";
				}
				postRating(widgetId, starNbr, curUser);
			}			

			function postRating(id, rating, user) {
		        var url = "${url.serviceContext}/someco/rating?id=" + id + "&rating=" + rating + "&guest=true&user=" + user;
		        new Ajax.Request(url, {
    				method:"post",
    				onSuccess: function(transport) {
      					var response = transport.responseText || "no response text";
      					alert("Success: \n\n" + response);
						window.location.reload(true);
    				},
    				onFailure: function(transport) {
    					var response = transport.responseText || "no response text";
    					alert("Failure: \n\n" + response);
    				}
				});
			}		
		
			function deleteRatings(id) {
		        var url = "${url.serviceContext}/someco/rating/delete?id=" + id;
		        new Ajax.Request(url, {
    				method:"post",
    				onSuccess: function(transport) {
      					var response = transport.responseText || "no response text";
      					alert("Success: \n\n" + response);
						window.location.reload(true);
    				},
    				onFailure: function(transport) {
    					var response = transport.responseText || "no response text";
    					alert("Failure: \n\n" + response);
    				}
				});

			}		
									
			Event.observe(window, 'load', initEvents);
		</script>

		<p><a href="${url.serviceContext}/someco/whitepapers.html?guest=true">Back to the list</a> of whitepapers</p>
		<p>Node: ${args.id}</p>
		<p>Average: ${rating.average}</p>
		<p># of Ratings: ${rating.count}</p>
		<#if (rating.user > 0)>
			<p>User rating: ${rating.user}</p>
		</#if>
		<form name="login">
			Rater:<input name="userId"></input>
		</form>
        Rating: <div class="rating" id="rating_${args.id}" style="display:inline">${rating.average}</div>
        <p><a href="#" onclick=deleteRatings("${args.id}")>Delete ratings</a> for this node</p>
	</body>
</html>
