#!/bin/bash

date -d today > /dev/null 2> /dev/null
if [ ! $? -eq 0 ];
then
  # this script cannot run in your environment. see:
  # http://www.unix.com/shell-programming-scripting/146216-date-difference-between-freebsd-linux.html
  echo "This script cannot run on BSD (for Linux only)."
  exit 1
fi

TEST_DIR=/tmp/test_remove_old_backup
rm -rf $TEST_DIR
mkdir $TEST_DIR

create_files() {
  pushd $TEST_DIR
  for DAY in {1..400}
  do
    FILE_NAME=`printf %03d $DAY`.dat
    FILE_DATE=`date +%Y%m%d%H%M --date "20110101 $DAY days ago"`
    touch $FILE_NAME -m -t $FILE_DATE
  done
  popd
}

check_files(){
  pushd $TEST_DIR
  if [ ! -e  001.dat ]; then echo " 001.dat should exist"; fi 
  if [ ! -e  002.dat ]; then echo " 002.dat should exist"; fi 
  if [ ! -e  003.dat ]; then echo " 003.dat should exist"; fi 
  if [ ! -e  004.dat ]; then echo " 004.dat should exist"; fi 
  if [ ! -e  005.dat ]; then echo " 005.dat should exist"; fi 
  if [ ! -e  006.dat ]; then echo " 006.dat should exist"; fi 
  if [ ! -e  007.dat ]; then echo " 007.dat should exist"; fi 
  if [ ! -e  010.dat ]; then echo " 010.dat should exist"; fi 
  if [ ! -e  017.dat ]; then echo " 017.dat should exist"; fi 
  if [ ! -e  024.dat ]; then echo " 024.dat should exist"; fi 
  if [ ! -e  031.dat ]; then echo " 031.dat should exist"; fi 
  if [ ! -e  061.dat ]; then echo " 061.dat should exist"; fi 
  if [ ! -e  092.dat ]; then echo " 092.dat should exist"; fi 
  if [ ! -e  122.dat ]; then echo " 122.dat should exist"; fi 
  if [ ! -e  153.dat ]; then echo " 153.dat should exist"; fi 
  if [ ! -e  184.dat ]; then echo " 184.dat should exist"; fi 
  if [ ! -e  214.dat ]; then echo " 214.dat should exist"; fi 
  if [ ! -e  245.dat ]; then echo " 245.dat should exist"; fi 
  if [ ! -e  275.dat ]; then echo " 275.dat should exist"; fi 
  if [ ! -e  306.dat ]; then echo " 306.dat should exist"; fi 
  if [ ! -e  334.dat ]; then echo " 334.dat should exist"; fi 
  if [ ! -e  365.dat ]; then echo " 365.dat should exist"; fi
  if [ `find $TEST_DIR -type f | wc -l` -gt 23 ]; then echo "unneccesary files are found"; fi
  echo testing is done.
  echo list of backup files which is not removed:
  ls -l $TEST_DIR
  popd
}

create_files
./remove_old_backups.sh "$TEST_DIR" "2010/12/31"
check_files
