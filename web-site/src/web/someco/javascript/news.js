function initNewsPage(basePath) {
  var newsLinks = $("tertiaryMenu").getElementsByTagName("a");
  for(var i=0; i < newsLinks.length; i++){
    newsLinks[i].onclick = function() { onNewsClick(this, basePath); return false; }
  }
  
  //if( newsLinks.length <= 1 ) { 
  //  return false;
  //}
  
  var hash = window.location.hash;
  if(hash) {
    loadNews(window.location.pathname.substr(1) + '/' + hash.substr(1), basePath);
  } else {
    onNewsClick(newsLinks[0], basePath);
  }
}

function onNewsClick(link, basePath) {
  var newsLink = link.href;
  /* split the link and take the last two tokens. the format is contenttype/title */
  var tokens = newsLink.split("/");
  var newsName = 'news/' + tokens[tokens.length-2]+"/"+tokens[tokens.length-1];
  loadNews(newsName, basePath);
  Element.scrollTo($("pageTitle"));
}

function loadNews(newsLink, basePath) {
  var newsName = newsLink.substr(newsLink.lastIndexOf('/') + 1);
  var newsContentPane = $("newsContent");
  var topFiles = $("topFiles");
  newsContentPane.update("Loading...");
  var newsContent = basePath + "ajaxnews/" + newsLink; 
  var myAjax = new Ajax.Updater(newsContentPane, newsContent, {method: "GET" });
  
  // Update hash on url location bar.
  window.location.hash = newsName.substr(newsName.lastIndexOf("/")+1);
    
  var newsLinks = $("tertiaryMenu").getElementsByTagName("a");
    
  // Find corresponding news link and mark it selected.
  for (var i=0; i<newsLinks.length; i++) {
    var linkLocation = newsLinks[i].href;
    var liNode = newsLinks[i].parentNode;
        
    if(linkLocation.indexOf(newsName) > 0) {
      liNode.className = "selected";
      var name = newsLinks[i].firstChild.nodeValue;
      // Update page title on the page and browser window.
      document.title = name;
    } else {
      liNode.className = "leaf";
    }
  }
}
