/*
 * Linulator - The Linux Simulator
 * Copyright (C) 2014 Lloyd Dilley
 * http://www.linulator.org/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ToDo: Create Entry objects to populate database and write them to the filesystem table.

// This class creates the initial system baseline from an existing Linux filesystem
class Baseline
{
  public static final long SECONDS_IN_A_YEAR = 31556926;

  public static void main(String[] args)
  {
    // Due to use of NIO
    double javaVersion = Double.valueOf(System.getProperty("java.specification.version"));
    if(javaVersion < 1.7)
    {
      System.err.println("Java >=7 is required.");
      System.exit(1);
    }

    if(!System.getProperty("os.name").equals("Linux"))
    {
      System.err.println("This must be run on a Linux system.");
      System.exit(1);
    }

    if(!System.getProperty("user.name").equals("root"))
    {
      System.err.println("This must be run as root.");
      System.exit(1);
    }

    if(args.length == 0)
    {
      System.err.println("Not enough arguments.\n");
      showUsage();
      System.exit(1);
    }

    if(args.length > 1)
    {
      System.err.println("Too many arguments.\n");
      showUsage();
      System.exit(1);
    }

    if(args[0].equals("-c"))
    {
      try
      {
        createBaseline();
      }
      catch(IOException ioe)
      {
        System.err.println(ioe.getMessage());
      }
    }
    else if(args[0].equals("-h"))
      showUsage();
    else
    {
      System.err.println("Invalid argument.\n");
      showUsage();
      System.exit(1);
    }
  }

  public static void createBaseline() throws IOException
  {
    Scanner scanner = new Scanner(System.in);
    String input = null;

    System.out.println("Warning: This should only be performed on a fresh install to avoid importing personal data!");
    System.out.print("Type \"yes\" and press enter if you understand this and want to proceed: ");
    input = scanner.nextLine();

    if(!input.equalsIgnoreCase("yes"))
    {
      System.out.println("Process aborted per user request.");
      System.exit(0);
    }

    FileVisitor<Path> fileProcessor = new ProcessFile();
    Files.walkFileTree(Paths.get("/"), fileProcessor);     // get everything under /
  }

  private static final class ProcessFile extends SimpleFileVisitor<Path>
  {
    @Override public FileVisitResult visitFile(Path file, BasicFileAttributes basicAttribs) throws IOException
    {
      PosixFileAttributes posixAttribs = Files.readAttributes(file, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      Set<PosixFilePermission> posixPerms = posixAttribs.permissions();
      String perms = PosixFilePermissions.toString(posixPerms);         // convert to symbolic format

      String inode = posixAttribs.fileKey().toString();
      Pattern p = Pattern.compile("\\=(\\d+)\\)");                      // regex to acquire just the inode
      Matcher m = p.matcher(inode);
      m.find();
      inode = m.group(1);

      long mtimeStamp = posixAttribs.lastModifiedTime().toMillis();

      if(posixAttribs.isSymbolicLink())
        System.out.println(Files.readSymbolicLink(file).toString());

      // Only files have a type (not directories)
      String type = Files.probeContentType(file).substring(0, 4);       // check for "text"
      String contents = null;

      if(type.equals("text"))
        contents = readText(file);
      else if(type.equals("inod"))
        System.out.println("File is a link.");
      else
        contents = readBin(file);

      // Log this to file once in production
      System.out.println("Properties of file: " + file);
      System.out.println(inode + " " + perms + " " + posixAttribs.owner().getName() + ":" + posixAttribs.group().getName() + " " + posixAttribs.size() + " " + calcMtime(mtimeStamp) + "\n");

      return FileVisitResult.CONTINUE;
    }

    @Override public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes basicAttribs) throws IOException
    {
      PosixFileAttributes posixAttribs = Files.readAttributes(directory, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      Set<PosixFilePermission> posixPerms = posixAttribs.permissions();
      String perms = PosixFilePermissions.toString(posixPerms);

      String inode = posixAttribs.fileKey().toString();
      Pattern p = Pattern.compile("\\=(\\d+)\\)");
      Matcher m = p.matcher(inode);
      m.find();
      inode = m.group(1);

      long mtimeStamp = posixAttribs.lastModifiedTime().toMillis();

      if(posixAttribs.isSymbolicLink())
        System.out.println(Files.readSymbolicLink(directory).toString());

      // Log this to file once in production
      System.out.println("Properties of directory: " + directory);
      System.out.println(inode + " " + perms + " " + posixAttribs.owner().getName() + ":" + posixAttribs.group().getName() + " " + posixAttribs.size() + " " + calcMtime(mtimeStamp) + "\n");

      return FileVisitResult.CONTINUE;
    }
  }

  public static String calcMtime(long mtimeStamp)
  {
    Date currentDate = new Date();
    Date mdate = new Date(mtimeStamp);
    long delta = currentDate.getTime() / 1000 - mdate.getTime() / 1000;
    SimpleDateFormat timestampFormat;

    // If mtime is >1 year, show MMM d YYYY instead
    if(delta >= SECONDS_IN_A_YEAR)
      timestampFormat = new SimpleDateFormat("MMM d  YYYY");
    else
      timestampFormat = new SimpleDateFormat("MMM d HH:mm");

    return timestampFormat.format(mdate);
  }

  public static String readBin(Path passedFile)
  {
    String data = null;
    File file = new File(passedFile.toString());
    FileInputStream targetFile;

    try
    {
      targetFile = new FileInputStream(file);
      byte[] content = new byte[(int)file.length()];
      targetFile.read(content);
      data = new String(content);
      targetFile.close();
    }
    catch(IOException ioe)
    {
      System.err.println("Unable to read " + passedFile.toString() + ":");
      System.err.println(ioe.getMessage());
    }

    return data;
  }

  public static String readText(Path passedFile)
  {
    String data = null;

    BufferedReader targetFile;
    String line;

    try
    {
      targetFile = new BufferedReader(new FileReader(passedFile.toString()));
      while((line = targetFile.readLine()) != null)
      {
        data += line;
      }
      targetFile.close();
    }
    catch(IOException ioe)
    {
      System.err.println("Unable to read " + passedFile.toString() + ":");
      System.err.println(ioe.getMessage());
    }

    return data;
  }

  public static void writeDatabase(Entry item)
  {
    // 
  }

  public static void showUsage()
  {
    System.out.println("Baseline Usage");
    System.out.println("==============");
    System.out.println("-c\tCreate system baseline");
    System.out.println("-h\tDisplays this help information\n");
  }

  class Entry
  {
    // file properties that are shoved into the database
  }
}
