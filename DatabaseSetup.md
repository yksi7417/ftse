# MySQL #

An instance of MySQL is required to **default** run the project. If you have the knowledge you can configure whatever database you'd like through the **Spring** configurations in the project.

If you just want to give it a try, you can install an instance of MySQL from [here](http://dev.mysql.com/downloads/mysql/5.0.html).


# Database Setup #
To initialize a database:
  1. Login as the **root** user or any user with sufficient privileges
  1. Create a database(catalog) named **ftse**
  1. Run the script under **src/main/resources/ftse.sql**

# Database User/Password #
If you need to use a different user or password with the database, you can configure it in configuration file under **src/main/resources/configs/ftse-config.xml** for the bean named **dataSource**.