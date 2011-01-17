var data;

function getWhitepapers(url) {
	var AjaxObject = {
	
		handleSuccess:function(o){
			this.processResult(o);
		},
	
		handleFailure:function(o){
			// Failure handler
			alert("Failure");
		},
	
		processResult:function(o){
			
			try {
				data = YAHOO.lang.JSON.parse(o.responseText);
				var el = new YAHOO.util.Element("whitepapers-menu");
				el.on('contentReady', function() {
					for (var i=0; i<data.whitepapers.length; i++) {
						var whitepaper = data.whitepapers[i];
						var li = document.createElement('li');
						var a = document.createElement('a');
						a.id = "whitepaper-" + i;
						var div = document.createElement('div');
						div.id = "rating_" + whitepaper.id;
						var ratingValue = document.createTextNode(whitepaper.rating.average);
						div.appendChild(ratingValue);
						YAHOO.util.Dom.addClass(div, 'rating');
						a.href = "javascript:loadWhitepaper(" + i + ");";
						a.innerHTML = whitepaper.title;
						a.appendChild(div);
						li.appendChild(a);
						YAHOO.util.Dom.addClass(li, 'leaf');
						el.appendChild(li);
					}
					init_rating("http://localhost:8080/alfresco/service/someco/rating");
				});

			} catch(e) {
				alert("Invalid JSON string");
			}
		},
	
		startRequest:function(url) {
		   YAHOO.util.Connect.asyncRequest('GET', url, callback, null);
		}
	};
	
	/*
	 * Define the callback object for success and failure
	 * handlers as well as object scope.
	 */
	var callback =
	{
		success:AjaxObject.handleSuccess,
		failure:AjaxObject.handleFailure,
		scope: AjaxObject
	};

	AjaxObject.startRequest(url);
}

function loadWhitepaper(index) {
	var whitepaper = data.whitepapers[index];
	var downloadLink = document.getElementById("topFiles");
	var div = document.createElement('div');
	div.id = "csTopDownload";
	var link = document.createElement('a');
	link.href = whitepaper.link;
	var img = document.createElement('img');
	img.src = whitepaper.icon;
	var linkText = document.createTextNode('Download Whitepaper');
	link.appendChild(img);
	link.appendChild(linkText);
	div.appendChild(link);
	
	if (downloadLink.hasChildNodes()) {
		downloadLink.replaceChild(div, downloadLink.firstChild);
	} else {
		downloadLink.appendChild(div);
	}
	
	var whitepaperDescription = document.getElementById("newsContent");
	var descriptionDiv = document.createElement('div');
	YAHOO.util.Dom.addClass(descriptionDiv, 'node');
	var descriptionTitle = document.createElement('h2');
	descriptionTitle.innerHTML = whitepaper.title;
	var descriptionText = document.createElement('p');
	descriptionText.innerHTML = whitepaper.description;
	descriptionDiv.appendChild(descriptionTitle);
	descriptionDiv.appendChild(descriptionText);
	
	if (whitepaperDescription.hasChildNodes()) {
		whitepaperDescription.replaceChild(descriptionDiv, whitepaperDescription.firstChild);
	} else {
		whitepaperDescription.appendChild(descriptionDiv);
	}	
}