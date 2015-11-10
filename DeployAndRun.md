Before you can deploy and run the project you need to have setup the database required for the application. Also, it is assumed that you configured your eclipse with Maven.

# GWT Compile #

Before you could deploy and run the project, you need to **compile** the GWT user interface of the application. To do so:
  1. Open the project in eclipse.
  1. Through **Ant** view in eclipse, load the **build.xml** in the project
  1. Run the task named **gwtc**.

# Deploy and Run #
To run the application:
  1. Open a console and go to your project root in console
```
cd /path/to/project
```
  1. First, it would be nice to clean the workspace
```
mvn clean
```
  1. You can easily deploy and run:
```
mvn tomcat:run
```
  1. To test the application go to http://localhost:8080/dbdm-ftse/gwt/nl.liacs.dbdm.ftse.ui.FtseModule/index.html
