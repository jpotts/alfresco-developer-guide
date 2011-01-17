The client-extensions project can be imported as an existing Eclipse project into the Eclipse IDE. If the Alfresco SDK has been imported into the same Eclipse workspace, the project will have everything it needs to build successfully.

Building the project with Ant

The project includes an build.xml and a build.properties. If you do not modify the project folder structure, you should only have to modify the build.properties file to specify the location of the Alfresco SDK and the Alfresco web application root folder.

Chapter 2 Examples

Chapter 2 included two examples. Both examples implemented two small customizations: a custom login page and a few simple custom types. The examples showed how to package and deploy these customizations. One example does it by copying the customizations into an exploded Alfresco web application. The other does it by packaging an AMP file and then calling the MMT to install the AMP into a WAR.

Chapter 3 Examples

Chapter 3 includes extending the content model with several types, aspects, and associations. The model is exposed to the UI through web-client-config-custom. Several runnable web services examples, a PHP example, and JavaScript examples are also included.

To deploy without AMP:

1. Make sure build.properties reflects the appropriate file paths.
2. Run "ant deploy".

To deploy as an AMP:

1. Make sure build.properties reflects the appropriate file paths.
2. Copy the alfresco.war to the client-extensions/build directory if it isn't there already.
3. Run "ant deploy-amp -Dalfresco.war.path=[path to your workspace]/client-extensions/build/alfresco.war".

To run the Web Services examples:

Either run the classes from Eclipse, remembering to set the arguments appropriately, or, run the associated Ant targets from the command-line.