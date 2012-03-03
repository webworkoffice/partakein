#!/bin/bash

BACKUP_DIR=$1
SPECIFIED_DAY=`date +%s -d $2`
TODAY=`date +%s`
OFFSET=`echo "($TODAY-$SPECIFIED_DAY)/60/60/24" | bc`

contains() {
  TARGET=$1
  shift
  ENTRY=$1
  shift
  while [ $? -eq 0 ]
  do
    if [ $ENTRY -eq $TARGET ]
    then
      return 0
    fi
    ENTRY=$1
    shift
  done
  return 1
}

remove(){
  DAY_COUNT=`echo $1+$OFFSET | bc`
  shift

  for FILE in `find "$BACKUP_DIR" -type f -mtime +$DAY_COUNT`
  do
    DAY=`ls -l "$FILE" | awk '{print $6}' | awk 'BEGIN{FS="-"}{print $3}'`
    contains $DAY $@
    if [ $? -eq 0 ]
    then
      : # its day of month is specified, so keep this file as weekly/monthly backup
    else
      rm "$FILE"
    fi
  done
}

# remove dumps which is than 1 year old 
remove 365

# remove dumps which is than 1 month old, exclude monthly backup
remove 30  1

# remove dumps which is than 7 days old, exclude weekly backup
remove 7   1 8 15 22 29

