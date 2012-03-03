#!/bin/sh

~/backup.sh

/bin/rm -rf /var/lib/tomcat6/webapps/ROOT
/bin/cp ~/ROOT.war /var/lib/tomcat6/webapps/


/etc/init.d/apache2 stop
/etc/init.d/tomcat6 restart
/bin/sleep 8
/etc/init.d/apache2 start

