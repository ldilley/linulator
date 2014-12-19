About
=====
The Snapshot program creates a snapshot of a Linux system and stores all the information in a database.
This database serves as the virtual filesystem for a Linulator instance.

Requirements
============
1.) Java 7 or higher
2.) Run as root
3.) The operating system must be Linux
4.) Apache Derby (http://db.apache.org/derby/)

Compiling
=========
The program can simply be compiled with: javac Snapshot.java

Usage
=====
The program has two options:
-c      Create snapshot
-h      Display help

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

10.) Move this directory to the "tools/" directory where the Snapshot program resides:
     mv linulator/ /path/to/linulator/tools/

11.) Create a snapshot of the running operating system (* Note: This will take some time!):
     java -cp .:../lib/derby.jar Snapshot -c

12.) Move the "linulator/" database directory to the "dist/" directory where the linulator.jar resides:
     mv linulator/ ../dist/

13.) Fire up Linulator and enjoy your new instance:
     java -jar linulator-x.x.x-x.jar
