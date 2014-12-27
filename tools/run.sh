#!/bin/sh

DERBY_JAR="../lib/derby.jar"

if [ ! -f CloneFilesystem.class ]; then
  javac CloneFilesystem.java
fi

if [ ! -f $DERBY_JAR ]; then
  printf "${DERBY_JAR} not found! Please set the \$DERBY_JAR variable in run.sh appropriately.\n"
  exit 1
fi

java -cp .:$DERBY_JAR CloneFilesystem -c
