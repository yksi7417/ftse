# Eclipse + Maven Integration #

## Maven ##

  1. Download Maven http://maven.apache.org/download.html
  1. Setup Maven very quickly: http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
  1. You need to define an environment variable **M2\_HOME**; in **~/.bash\_profile** or  **~/.profile**, add the following line:
```
export M2_HOME=/path/to/apache-maven
export PATH=$PATH:$M2_HOME/bin
```
  1. Create a directory under **M2\_HOME/repo**
  1. Edit settings file under **M2\_HOME/conf/settings.xml** and add a local repository:
```
   <localRepository>/path/to/apache-maven/repo</localRepository>
```
  1. Create a **symbolic link** to the Maven repository by:
```
ln -s /path/to/apache-maven/repo ~/.m2/repository
```

## Eclipse + Maven Integration ##

### Maven Repository ###

You only need to define a **Classpath Variable** in eclipse to do so
  1. Go to **Window -> Preferences**
  1. In the left side tree, go to **Java -> Build Path -> Classpath Variables**
  1. Click **New...**
  1. Variable name would be **M2\_REPO** and the value **~/.m2/repository**
  1. If you already have it then you're done.

### Maven Dependency Management ###

To update the dependencies:

  1. Open a terminal and go to **/path/to/eclipse/project**
  1. Run
```
mvn eclipse:eclipse
```

To have the dependency sources:
  1. Open a terminal and go to **/path/to/eclipse/project**
  1. Run
```
mvn dependency:sources
mvn eclipse:eclipse
```