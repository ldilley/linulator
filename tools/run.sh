#!/bin/sh
if [ ! -f CloneFilesystem.class ]; then
  javac CloneFilesystem.java
fi
java -cp .:../lib/derby.jar CloneFilesystem -c
