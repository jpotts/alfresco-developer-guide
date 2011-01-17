The web-site project can be imported as an existing Eclipse project into the Eclipse IDE.

Building the project with Ant

The project includes an build.xml and a build.properties. If you do not modify the project folder structure, you should only have to modify the build.properties file to specify the location of the target web root. If you use the Alfresco web application you won't have to deal with the host-related scripting issues. If you want to work through those on your own, specify an Apache or Tomcat root separate from Alfresco.