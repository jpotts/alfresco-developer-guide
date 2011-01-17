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

MPXJ was upgraded to 3.0.0 in this version of the source.

To deploy:

1. Make sure build.properties reflects the appropriate file paths.
2. Run "ant deploy".