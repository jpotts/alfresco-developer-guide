<?php

  require_once "Alfresco/Service/Session.php";
  require_once "Alfresco/Service/SpacesStore.php";
  require_once "Alfresco/Service/Node.php";

  global $username;
  global $password;
  global $rootFolder;
  global $contentType;
  global $contentName;

  if (array_key_exists('_submit_check', $_POST)) {
	$username = $_POST['username'];
	$password =$_POST['password'];
	$rootFolder = $_POST['rootFolder'];
	$contentType = $_POST['contentType'];
	$contentName = $_POST['contentName'];

	if ($username != "" && $password != "") {
		createContent($username, $password, $rootFolder, $contentType, $contentName);
	}
  }

  function createContent($username, $password, $rootFolder, $contentType, $contentName) {

	// Start and create the session
	$repository = new Repository("http://localhost:8080/alfresco/api");
	$ticket = $repository->authenticate($username, $password);
	$session = $repository->createSession($ticket);

	$store = new Store($session, "SpacesStore");

	$folderPath = "/app:company_home/cm:" . $rootFolder;

	// Grab a reference to the SomeCo folder
	$results = $session->query($store, 'PATH:"' . $folderPath . '"');
	$rootFolderNode = $results[0];

	if ($rootFolderNode == null) {
		echo "Root folder node (" . $folderPath . ") is null<br>";
		exit;
	} 

	$timestamp = time();
        
	$newNode = $rootFolderNode->createChild("{http://www.someco.com/model/content/1.0}" . $contentType, "cm_contains", "{http://www.someco.com/model/content/1.0}" . $contentType . "_" . $timestamp );

	if ($newNode == null) {
		echo "New node is null<br>";
		exit;
	}

	// Add the two aspects
	$newNode->addAspect("{http://www.someco.com/model/content/1.0}webable");
	$newNode->addAspect("{http://www.someco.com/model/content/1.0}clientRelated");

	echo "Aspects added<br>";

	// Set the properties
	$properties = $newNode->getProperties();

	$properties["{http://www.alfresco.org/model/content/1.0}name"] = $contentName . " (" . $timestamp . ")";
	$properties["{http://www.someco.com/model/content/1.0}isActive"] = "true";
	$properties["{http://www.someco.com/model/content/1.0}published"] = "2007-04-01T00:00:00.000-05:00";

	$newNode->setProperties($properties);

	echo "Props set<br>";

	$newNode->setContent("cm_content", "text/plain", "UTF-8", "This is a sample " . $contentType . " document named " . $contentName);

	echo "Content set<br>";

	$session->save();

	echo "Saved changes to " . $newNode->getId() . "<br>";
  }

?>

<html>
	<head>
		<title>SomeCoDataCreator Alfresco PHP Example</title>
	</head>
	<body>
		<form method="POST"  action="<?php echo $_SERVER['PHP_SELF']; ?>">		
			Username: <input name="username" value="<?php if ($username == "") {echo "admin";}else{echo $username;} ?>" /><br />
			Password: <input name="password" value="<?php if ($password == "") {echo "admin";}else{echo $password;} ?>" /><br />
			Folder: <input name="rootFolder" value="<?php if ($rootFolder == "") {echo "SomeCo";}else{echo $rootFolder;} ?>" /><br />
			Type: <input name="contentType" value="<?php if ($contentType == "") {echo "doc";}else{echo $contentType;} ?>" /><br />
			Name: <input name="contentName" value="<?php if ($contentName == "") {echo "Test Document";}else{echo $contentName;} ?>" /><br />
			<input type="submit" value="Create Content" />
			<input type="hidden" name="_submit_check" value="1"/> 
		</form>
	</body>
</html>
