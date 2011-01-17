The client-extensions project can be imported as an existing Eclipse project into the Eclipse IDE. If the Alfresco SDK has been imported into the same Eclipse workspace, the project will have everything it needs to build successfully.

Building the project with Ant

The project includes an build.xml and a build.properties. If you do not modify the project folder structure, you should only have to modify the build.properties file to specify the location of the Alfresco SDK and the Alfresco web application root folder.

Chapter 2 Examples

Chapter 2 included two examples. Both examples implemented two small customizations: a custom login page and a few simple custom types. The examples showed how to package and deploy these customizations. One example does it by copying the customizations into an exploded Alfresco web application. The other does it by packaging an AMP file and then calling the MMT to install the AMP into a WAR.

Chapter 3 Examples

Chapter 3 includes extending the content model with several types, aspects, and associations. The model is exposed to the UI through web-client-config-custom. Several runnable web services examples, a PHP example, and JavaScript examples are also included.

To run the Web Services examples:

Either run the classes from Eclipse, remembering to set the arguments appropriately, or, run the associated Ant targets from the command-line.

Chapter 4 Examples

Chapter 4 includes writing custom actions, behaviors (both Java and JavaScript), server-side JavaScript, metadata extracters, and content transformers.

The transformer example transforms MS Project files to plain text. It relies on a third-party library called mpxj.jar. That library depends on POI which is shipped with Alfresco. Over time, as Alfresco ships with newer versions of POI, a newer version of the MPXJ library may be required. 

Chapter 5 Examples

Chapter 5 is about customizing the out-of-the-box Alfresco client (aka "Explorer"). It includes examples for creating UI actions, hiding UI actions based on groups and permissions, overriding OOTB renderer settings, creating a custom renderer, and creating custom dialogs and wizards. The "execute script" dialog example is a handy shortcut you might like to use on projects where you find yourself running a lot of server-side JavaScript against nodes from the Explorer client.

Chapter 6 Examples

Chapter 6 is on web scripts. It starts with a simple hello world example and moves on to a RESTful API for getting whitepapers and for getting, posting, and deleting ratings for a node. Examples showing how to process form data and file uploads from a web script are also provided.

If you want to try the AJAX web script example, you'll also need to deploy the "someco-web-site" project.

Note that addTestRating.js changed from Chapter 5 to Chapter 6. If you still have the version from Chapter 5 in your repository, you'll want to upgrade it with the version from this chapter. Ratings created using the chapter 5 version cannot be deleted with the web script created in this chapter. If you have old ratings hanging around, delete them through the web client.

Chapter 7 Examples

Chapter 7 is about advanced workflows using the jBPM engine. It includes some hello world processes that demonstrate forks and decisions, a process to set the Someco web flag, and a process that shows how to interact with a workflow from a web script. In this case the technique is used to allow a non-Alfresco user to participate in the workflow.

In 2.2 Enterprise, there is a known issue with workflow timers and "runas" so that example may not work properly.

When opening the process definitions, if you are prompted by the JBPM GPD to "convert and open", that's okay. That happens when your version of the GPD plug-in is newer than the one that created the processes. 

To deploy:

1. Make sure build.properties reflects the appropriate file paths.
2. Run "ant deploy".