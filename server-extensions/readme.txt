The someco-server-extensions project can be imported as an existing Eclipse project into the Eclipse IDE. If the Alfresco SDK has been imported into the same Eclipse workspace, the project will have everything it needs to build successfully.

Building the project with Ant

The project includes an build.xml and a build.properties. If you do not modify the project folder structure, you should only have to modify the build.properties file to specify the location of the Alfresco SDK, Alfresco web application root, and Alfresco "shared" folders.

In prior versions of this source code, enterprise and community configs were in the same project. When you did the deploy, you specified which flavor you wanted. That's no longer the case now that the source code has been split by chapter, by version.

WARNING!

This project contains the files you would typically put in your Tomcat shared extension folder. That's things like your alfresco-global.properties file. If you have been maintaining your own set of props files and have not imported those files into this project, DO NOT DEPLOY THIS PROJECT. It will overwrite your custom settings that are sitting in Alfresco shared extensions.

To deploy:

1. Make sure build.properties reflects the appropriate file paths.
2. Run "ant deploy".

Known working LDAP setups

This config (with minor tweaks for schema differences) has been known to work with both OpenLDAP and Apache Directory Server. If you use the LDIF files in the data directory to build your initial context in your LDAP directory, note the directory-specific root.ldif files.

Known working CAS setups

The CAS-related config in this project has been known to work with:

JA-SIG CAS Server 3.2.1 + Yale CAS Client 2.1.1
JA-SIG CAS Server 3.3.3 + Yale CAS Client 2.1.1

It will not work as-is with the JA-SIG CAS Client for Java.

The SSO approach outlined in Chapter 9 no longer works as-is with Alfresco 3.2 Enterprise.