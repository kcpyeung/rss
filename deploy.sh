#!/bin/bash

if [ $# -ne 2 ]
then
  echo 'usage: deploy.sh [host] [port]'
  exit 1
fi

host=$1
port=$2
echo "deploying to $host:$port"

echo 'compiling'
gradle build

echo 'copying'
scp -C ./build/libs/rss.jar ubuntu@$host:~
scp ./*.yaml ubuntu@$host:~

echo 'starting server remotely'
ssh $host "sudo killall java"
ssh $host "nohup sudo java -jar rss.jar $port </dev/null >/dev/null 2>&1 &"
