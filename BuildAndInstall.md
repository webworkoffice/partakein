# Introduction #

This page describes how to build PARTAKE on eclipse and run on Tomcat 6.

# Details #
## Checkout and build PARTAKE ##
We use eclipse 3.6 (Java EE platform) with [m2eclipse](http://m2eclipse.sonatype.org/sites/m2e) plugin.
  1. checkout PARTAKE from http://code.google.com/p/partakein/source/checkout
    * "http://partakein.googlecode.com/svn/trunk/Partake" is project's root dir.
  1. On eclipse, select File -> New -> Dynamic Web Project. Set Project name as you like (we recommend to use "Partake"), and set Project location where you saved the file. Then press Next >.
    * set Target runtime to Apache Tomcat v6.0
    * set Dynamic web module version to 2.5
  1. Now we are on "build path" page, set "Default output folder" as "target/classes". Then press Next >.![http://partake-stat.appspot.com/wiki/outputDir.png](http://partake-stat.appspot.com/wiki/outputDir.png)
  1. set "Context root" as "/", set "Content directory" as "src/main/webapp", and uncheck "Generate web.xml deployment descriptor". Then press Finish. ![http://partake-stat.appspot.com/wiki/webmodule.png](http://partake-stat.appspot.com/wiki/webmodule.png)
  1. If you are using Windows, do not forget to set project encoding to utf-8.
  1. right click on Partake project, and choose Maven -> enable project management.
  1. right click on Partake project, and choose Maven -> update project configuration. This may take a minute.
  1. **On Markers pane, you may see Classpath Dependency Validator Message.**
    * **In this case, right click it and select Quick fix, and press Finish. This will fix the problem.**
    * This is important. If you forget to do this, you will see ClassNotFoundException.
  1. If you can't build your project. Check the build path again.
    * We recommend to use JDK1.6 for Partake.
    * set "Default output folder:" to "{PROJECT\_NAME}/target/classes".
    * src/main/java, src/main/resources, src/test/java, and src/test/resources should be included. ![http://partake-stat.appspot.com/wiki/buildPath.png](http://partake-stat.appspot.com/wiki/buildPath.png)
  1. Then choose Run on Server. Since you have not configure PARTAKE, you will see an error message.

## setup Database ##

> When running PARTAKE, you can choose either Cassandra 0.6 or RDB as database. You should do either of these.
> However, currently only RDB will work well. Cassandra 0.6 might not work.

### setup Cassandra ###

  1. Download Apache Cassandra (0.6.5) and run on localhost. Maybe you have to set up data and index directories.
    * Cassandra use 8080 for JMX by default. This conflicts with tomcat's default port. So the port described in cassandra's launching script (e.g. conf/cassandra.sh.in) should be changed. We recommend to use port 8081 or something like that for JMX.
    * configure conf/storage-conf.xml to use OrderPreservingPartitioner. Find the line containing ` <Partitioner>org.apache.cassandra.dht.RandomPartitioner</Partitioner> ` and change it to ` <Partitioner>org.apache.cassandra.dht.OrderPreservingPartitioner</Partitioner> `.

### setup RDB ###

> Currently only PostgreSQL is tested, however PARTAKE should work with any other RDBs that is supported by OpenJPA (e.g. MySQL, DB2 and ORACLE).

  1. Install your preferred RDB.
  1. create a database having name 'partake'

## edit properties files ##
  1. fill src/main/resource/sample.partake.properties (especially bitly)
    * a partake.properties file has in.partake.mode property. The value of this property is called "mode".
    * partake will read {mode}.partake.properties.
  1. rename src/main/resource/twitter4j.properties.sample as src/main/resource/twitter4j.properties.
    * set your project name consumer key and consumer secret given from twitter.
  1. set "in.partake.database.daofacotry" and "in.partake.database.pool" properly.
    * If you have chosen to use Cassandra, set them like the following.
```
in.partake.database.daofactory=in.partake.model.dao.cassandra.CassandraDAOFactory
in.partake.database.pool=in.partake.model.dao.cassandra.CassandraConnectionPool
```
    * If you have chosen to use RDB, set them like the following.
```
in.partake.database.daofactory=in.partake.model.dao.jpa.JPADAOFactory
in.partake.database.pool=in.partake.model.dao.jpa.JPAConnectionPool
```
      * Also, you have to setup "persistence.xml" properly.
      * Especially, set the driver name, username, and password in ConnectionProperties like the following.
```
<property name="openjpa.ConnectionProperties" value="DriverClassName=org.postgresql.Driver,Url=jdbc:postgresql:partake,Username=partake,Password=partake"/>
```

## edit server.xml ##
You should add URIEncoding and useBodyEncodingForURI in your Connector.
```
    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443"
      		   URIEncoding="UTF-8" useBodyEncodingForURI="true" />
```

If you are just testing PARTAKE, 8080 port may help you. Do not forget to add URIEncoding and useBodyEncodingForURI in the Connector.
```
    <Connector connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"
    		   URIEncoding="UTF-8" useBodyEncodingForURI="true"
    />
```

And probably you should set mod\_ajp in your Apache HTTP server.

## Register your application ##
Do not forget to register your application to the twitter.
  * http://twitter.com/apps/new
  * Application Type should be "Browser"
  * Callback URL is any valid URL. But do not use localhost. This will rejected by twitter.
  * Default Access Type should be "Read & Write".
    * We don't need DirectMessage Read.
  * Check "Yes, use Twitter for login"

Also, please register your application to bit.ly. Without this, PARTAKE will work. but shows error messages in console.

## DONE! ##

Then PARTAKE should run now. If you have any difficulties, please tell @partakein or @mayahjp.


## NOTE ##

When you failed eclipse operations, web.xml may be rewritten. In this case, please revert web.xml.

## FOR RELEASE PACKAGING ##

We can use eclipse or console to make the release package.

In console, just type
```
$ mvn war:war
```

If you are using JPA instead of Cassandra, just type
```
$ mvn openjpa:enhance
$ mvn war:war
```