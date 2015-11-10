# Eclipse #
First, you need an instance of eclipse for development. For this, you can some updated version of the platform such as [Original Eclipse](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/galileor) or [SpringSource Tool Suite](http://www.springsource.com/products/sts).

# Eclipse + Subversion #

## Subversion Plugin ##
When your eclipse is ready, then please install the Subversion required library from http://subclipse.tigris.org/servlets/ProjectProcess?pageID=p4wYuA to have subclipse enabled in your eclipse.

## Subclipse Plugin Configuration ##
After you have your subclipse feature enabled in your eclipse, you just need to do some slight configuration for Subverstion:
  1. In eclipse menu, **Window**, choose **Preferences**
  1. Under the preference tree, find **Team** and then **SVN**
  1. Under **SVN**, in the right pane, find **SNV Interface** and change it to **SVNKit (Pure Java)...**
  1. Click OK and you're done.

# Source Checkout #
To check out the source:
  1. In eclipse menu, **File -> Import ...**
  1. Under **SVN** choose **Check out project from SVN**, and click **Next**
  1. Choose **Create a new repository location** and click **Next**
  1. Under **URL**, enter **https://ftse.googlecode.com/svn/trunk/** and click **Next**
  1. You're asked for your Google Code password which you can find [here](https://code.google.com/hosting/settings).
  1. Checkout the project as a new or an existing one that you have.

Now you can continue to [next step](http://code.google.com/p/ftse/wiki/EclipseMavenIntegration).