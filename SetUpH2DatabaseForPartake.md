# Introduction #

Description to set up PARTAKE with [H2 database](http://www.h2database.com/) for Windows.



# Detail #
## Install H2 database ##

[Download](http://www.h2database.com/html/download.html) and install it.

## Deploy PARTAKE ##

Deploy PARTAKE to Web Application Server like Tomcat6.
To create war file, you can use Maven like below at top directory:
```
 mvn clean package openjpa:enhance war:war
```

## Create administrator and schema ##
[Run h2 console](http://www.h2database.com/html/tutorial.html#tutorial_starting_h2_console) and login as System Administrator like SA.
Take care that JDBC url means a data file's path.
![http://partake-stat.appspot.com/wiki/loginConsole.png](http://partake-stat.appspot.com/wiki/loginConsole.png)


Execute below SQL and create administrator and schema.
```
CREATE USER partake PASSWORD 'partake';
ALTER USER partake ADMIN TRUE;
CREATE SCHEMA partake AUTHORIZATION partake;
```

## Copy library to %CATALINA\_HOME%\lib ##
Copy `h2-<version>.jar` to %CATALINA\_HOME%\lib.
This library is included in H2 database.

## Rewrite persistence.xml ##
Open WEB-INF\classes\META-INF\persistence.xml and rewrire **openjpa.ConnectionProperties**, **openjpa.jdbc.DBDictionary** and **openjpa.jdbc.Schema** property.
```
<persistence-unit name="partake">
  ...
  <properties>
    <property name="openjpa.ConnectionDriverName" value="org.apache.commons.dbcp.BasicDataSource"/>
    <property name="openjpa.ConnectionProperties" value="DriverClassName=org.h2.Driver,Url=jdbc:h2:file:C:/work/partake.h2,Username=partake,Password=partake"/>
    <property name="openjpa.Log" value="DefaultLevel=WARN, Tool=INFO"/>
    <property name="openjpa.jdbc.Schema" value="partake" />

    <property name="openjpa.jdbc.DBDictionary" value="org.apache.openjpa.jdbc.sql.H2Dictionary" />

    <property name="openjpa.jdbc.SynchronizeMappings" value="buildSchema(SchemaAction='add')" />
    <property name="openjpa.RuntimeUnenhancedClasses" value="unsupported"/>

    <property name="openjpa.DataCache" value="true"/>
    <property name="openjpa.QueryCache" value="false"/>
    <property name="openjpa.RemoteCommitProvider" value="sjvm"/>
  </properties>
  ...
```

## Startup Tomcat ##
Finally, kick %CATALINA\_HOME%\bin\startup.bat and access to http://localhost:8080/ .