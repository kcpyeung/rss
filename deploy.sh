#!/bin/bash

if [ $# -ne 1 ]
then
  echo 'usage: deploy.sh [host]'
  exit 1
fi

host=$1
echo "deploying to $host"

echo 'compiling'
gradle build

echo 'copying'
scp -C ./build/libs/rss.jar ubuntu@$host:~
scp ./*.yaml ubuntu@$host:~

echo 'starting server remotely'
ssh $host "sudo killall java; nohup sudo java -jar rss.jar 80 </dev/null >/dev/null 2>&1 &"
