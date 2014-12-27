About
=====
The CloneFilesystem program creates a replica of a Linux system and stores all the information in a database.
This database serves as the virtual filesystem for a Linulator instance.

Requirements
============
1.) Java 7 or higher
2.) Run as root
3.) The operating system must be Linux
4.) Apache Derby (http://db.apache.org/derby/)

Quick Start
===========
You can safely avoid the compilation and manual database creation steps below by letting Linulator create
the database and table information for you automatically. Just execute the "run.sh" script under the "tools/"
directory as root: ./run.sh

Compiling
=========
The program can simply be compiled with: javac CloneFilesystem.java

Usage
=====
The program has two options:
-c      Create clone
-h      Display help

Tips
====
1.) Use the "exclude.lst" file to configure directory and file exclusions. Regex is supported.
2.) Exercise caution to avoid importing anything personal such as private keys, passwords, etc.
3.) This program should *only* be used on a fresh Linux install with no important data.
4.) A lightweight install of a distribution is recommended for a quicker cloning process.
5.) If a file appears to be hanging, use ctrl+c and add the file to "exclude.lst".
6.) During a re-run of the import process, existing directories and files will be skipped.
7.) If you want to start over with a new database, simply recursively remove the "linulator/" database directory.

Database Initialization
=======================
1.)  Download the latest Derby from http://db.apache.org/derby/

2.)  Unpack the tarball: tar -zxvf db-derby-x.x.x.x-bin.tar.gz

3.)  Copy the filesystem SQL script to the "db-derby-x.x.x.x-bin/bin/" directory:
     cp sql/filesystem.sql /path/to/db-derby-x.x.x.x-bin/bin/

4.)  cd db-derby-x.x.x.x-bin/bin/

5.)  ./ij

6.)  Create the database using ij:
     ij> CONNECT 'jdbc:derby:linulator;create=true';

7.)  Create the table using ij:
     ij> run 'filesystem.sql';

8.)  Quit ij:
     ij> quit;

9.)  You should now have a "linulator/" directory containing data.

10.) Move this directory to the "tools/" directory where the CloneFilesystem program resides:
     mv linulator/ /path/to/linulator/tools/

11.) Create a clone of the running operating system (*Note: This will take some time!):
     java -cp .:../lib/derby.jar CloneFilesystem -c

12.) Move the "linulator/" database directory to the "dist/" directory where the linulator.jar resides:
     mv linulator/ ../dist/

13.) Fire up Linulator and enjoy your new instance:
     java -jar linulator-x.x.x-x.jar
