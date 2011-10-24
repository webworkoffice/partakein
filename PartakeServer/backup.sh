#!/bin/sh

LOG_FILE=~/backup/logs/backup_`/bin/date '+%Y%m%d_%H_%M_%S'`
BACKUP_WAR=~/backup/wars/ROOT-`/bin/date +%s`.war
BACKUP_SQL=~/backup/sqls/partake-`/bin/date +%s`.sql
BACKUP_MDL=~/backup/logs/middleware_`/bin/date '+%Y%m%d_%H_%M_%S'`.tar.gz
/bin/echo backup start >> $LOG_FILE 2>> $LOG_FILE
/bin/echo `/bin/date` >> $LOG_FILE 2>> $LOG_FILE

if ! [ -e ~/.pgpass ]; then
  /bin/echo '~/.pgpass does not exist.' >> $LOG_FILE 2>> $LOG_FILE
fi

/bin/cp ~/ROOT.war $BACKUP_WAR >> $LOG_FILE 2>> $LOG_FILE
/usr/bin/pg_dump partake -U partake > $BACKUP_SQL 2>> $LOG_FILE


TMP_LOG_DIR=/tmp/logs.d
/bin/rm -rf $TMP_LOG_DIR
/bin/mkdir $TMP_LOG_DIR

LOG_LIST=/tmp/logs.lst
/bin/rm -f $LOG_LIST

/bin/ls -1t /var/log/apache2/ | /bin/grep access.log | /usr/bin/head -n 6 | /usr/bin/awk '{print "/var/log/apache2/" $1}' >> $LOG_LIST
/bin/ls -1t /var/log/apache2/ | /bin/grep error.log  | /usr/bin/head -n 6 | /usr/bin/awk '{print "/var/log/apache2/" $1}' >> $LOG_LIST
/bin/ls -1t /var/log/apache2/ | /bin/grep mod_jk.log | /usr/bin/head -n 3 | /usr/bin/awk '{print "/var/log/apache2/" $1}' >> $LOG_LIST

/bin/ls -1t /var/log/tomcat6/ | /bin/grep catalina.2  | /usr/bin/head -n 3 | /usr/bin/awk '{print "/var/log/tomcat6/" $1}' >> $LOG_LIST
/bin/ls -1t /var/log/tomcat6/ | /bin/grep localhost.2 | /usr/bin/head -n 3 | /usr/bin/awk '{print "/var/log/tomcat6/" $1}' >> $LOG_LIST

/bin/ls -1t /var/log/postgresql/ | /bin/grep postgresql-8.4-main.log | /usr/bin/head -n 3 | /usr/bin/awk '{print "/var/log/postgresql/" $1}' >> $LOG_LIST

for LOG in `cat $LOG_LIST`
do
  /bin/cp $LOG $TMP_LOG_DIR
done

/bin/tar cvfz $BACKUP_MDL $TMP_LOG_DIR >> $LOG_FILE 2>> $LOG_FILE

TMP_BACKUP_DIR=/tmp/backups/
BACKUP_ALL=/tmp/backup.tar.gz
/bin/rm -rf $TMP_BACKUP_DIR
/bin/mkdir $TMP_BACKUP_DIR
/bin/cp $LOG_FILE $TMP_BACKUP_DIR
/bin/cp $BACKUP_WAR $TMP_BACKUP_DIR
/bin/cp $BACKUP_SQL $TMP_BACKUP_DIR
/bin/cp $BACKUP_MDL $TMP_BACKUP_DIR
/bin/tar cvfz $BACKUP_ALL $TMP_BACKUP_DIR >> $LOG_FILE 2>> $LOG_FILE

java \
    -jar S3MultipartTransportTool-0.1-jar-with-dependencies.jar \
    -b <<BUCKET_NAME>> \
    -c s3.properties \
    -e encryptionkey.txt \
    -l $BACKUP_ALL \
    -s backup`date '+%Y_%m_%d__%H_%M_%S'`.tar.gz \
    >> $LOG_FILE 2>> $LOG_FILE

/bin/rm -f $LOG_LIST
/bin/rm -rf $TMP_LOG_DIR
/bin/rm -rf $TMP_BACKUP_DIR
# /bin/rm -f /tmp/backup.tar.gz

/bin/echo backup end >> $LOG_FILE 2>> $LOG_FILE

