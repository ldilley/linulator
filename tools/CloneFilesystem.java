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
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class creates the initial system baseline from an existing Linux filesystem
class CloneFilesystem
{
  public static final String VERSION = "1.0";
  public static final long SECONDS_IN_A_YEAR = 31556926;
  private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
  private static String protocol = "jdbc:derby:";
  private static String databaseName = "linulator";
  private static Connection connection = null;
  private static PreparedStatement preparedStatement = null;
  private static Statement statement = null;
  private static ResultSet resultSet = null;
  private static Properties properties = new Properties();
  private static HashMap<String, Integer> users = new HashMap<String, Integer>();
  private static HashMap<String, Integer> groups = new HashMap<String, Integer>();

  public static class Record
  {
    String path;
    String name;
    long inode;
    byte type;
    int mode;
    int linkCount;
    int uid;
    int gid;
    long atime;
    long ctime;
    long mtime;
    String link;
    String data;
  }

  public static void main(String[] args)
  {
    System.out.println("\nCloneFilesystem " + VERSION + '\n');

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
        connectDatabase();
        readGroup();
        readPasswd();
        createClone();
        disconnectDatabase();
        shutdownDatabase();
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

  public static void createClone() throws IOException
  {
    Scanner scanner = new Scanner(System.in);
    String input = null;

    System.out.println("\n!!! This program _may_ not function as intended. Please report any hangs or bugs to the developers. !!!");
    System.out.println("Known issues:");
    System.out.println("1. Link counts are not currently supported. A default of '1' link (file itself) is recorded.");
    System.out.println("2. Setuid, setgid, and sticky bits are not currently supported.\n");
    System.out.println("Warning: This should only be performed on a fresh install to avoid importing personal data!");
    System.out.print("Type \"yes\" and press enter if you understand this and want to proceed: ");
    input = scanner.nextLine();

    if(!input.equalsIgnoreCase("yes"))
    {
      System.out.println("Process aborted per user request.");
      System.exit(0);
    }

    FileVisitor<Path> fileProcessor = new ProcessFile();
    Files.walkFileTree(Paths.get("/"), fileProcessor);   // get everything under /
  }

  private static final class ProcessFile extends SimpleFileVisitor<Path>
  {
    @Override public FileVisitResult visitFile(Path file, BasicFileAttributes basicAttribs) throws IOException
    {
      boolean skip = false;
      // ToDo: Allow arbitrary exclusions from a file to be read in
      if(file.toString().equals("/proc/kmsg") || file.toString().equals("/proc/kallsyms")) // these files cause hangs on reading
        skip = true;
      if(file.toString().equals("/proc/sys/vm/compact_memory") || file.toString().equals("/proc/sys/net/ipv4/route/flush")) // not readable
        skip = true;
      if(file.toString().equals("/proc/sys/net/ipv6/route/flush"))
        skip = true;
      if(file.toString().matches("/proc/[0-9]+/*.*")) // is a PID, so ignore it -- we'll generate our own PID data under /proc later
        skip = true;
      if(skip)
      {
        System.out.println("Ignored file: " + file);
        return FileVisitResult.CONTINUE;
      }

      System.out.println("Importing file: " + file);

      boolean isBinary = false;
      PosixFileAttributes posixAttribs = Files.readAttributes(file, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      Set<PosixFilePermission> posixPerms = posixAttribs.permissions();
      String perms = PosixFilePermissions.toString(posixPerms);         // convert to symbolic format

      String inode = posixAttribs.fileKey().toString();
      Pattern p = Pattern.compile("\\=(\\d+)\\)");                      // regex to acquire just the inode
      Matcher m = p.matcher(inode);
      m.find();
      inode = m.group(1);

      // Only files have a type (not directories)
      String type = Files.probeContentType(file).substring(0, 4);       // check for "text"
      String contents = null;

      if(type.equals("text"))
        contents = readText(file);
      else if(type.equals("inod"))
        contents = "";             // is a hard link
      else
      {
        isBinary = true;
        contents = readBin(file);
      }

      // Log this to file once in production
      //System.out.println("Properties of file: " + file);
      //System.out.println(inode + " " + perms + " " + posixAttribs.owner().getName() + ":" + posixAttribs.group().getName() + " " + posixAttribs.size() + " " + calcMtime(mtimeStamp) + "\n");

      CloneFilesystem.Record entry = new CloneFilesystem.Record();
      entry.path = file.toString();
      entry.name = new File(entry.path).getName();
      entry.inode = Long.parseLong(inode);
      entry.type = determineType(file, posixAttribs);
      entry.mode = calculateOctalPerms(perms);
      entry.linkCount = 1;                                      // link count is currently broken; set a default of one (itself)
      entry.uid = users.get(posixAttribs.owner().getName());    // use containsKey() if this causes a null pointer exception
      entry.gid = groups.get(posixAttribs.group().getName());   // same as above
      entry.atime = posixAttribs.lastAccessTime().toMillis();
      entry.ctime = posixAttribs.creationTime().toMillis();
      entry.mtime = posixAttribs.lastModifiedTime().toMillis();
      if(posixAttribs.isSymbolicLink())
        entry.link = Files.readSymbolicLink(file).toString();
      else
        entry.link = null;
      entry.data = contents;
      if(isBinary)
        writeDatabase(entry, true, false);
      else
        writeDatabase(entry, false, false);

      return FileVisitResult.CONTINUE;
    }

    @Override public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes basicAttribs) throws IOException
    {
      boolean skip = false;
      if(directory.toString().matches("/proc/[0-9]+/*.*")) // is a PID, so ignore it
        skip = true;
      if(skip)
      {
        System.out.println("Ignored directory: " + directory);
        return FileVisitResult.CONTINUE;
      }

      System.out.println("Importing directory: " + directory);

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
      //System.out.println("Properties of directory: " + directory);
      //System.out.println(inode + " " + perms + " " + posixAttribs.owner().getName() + ":" + posixAttribs.group().getName() + " " + posixAttribs.size() + " " + calcMtime(mtimeStamp) + "\n");

      CloneFilesystem.Record entry = new CloneFilesystem.Record();
      entry.path = directory.toString();
      entry.name = new File(entry.path).getName();
      entry.inode = Long.parseLong(inode);
      entry.type = determineType(directory, posixAttribs);
      entry.mode = calculateOctalPerms(perms);
      entry.linkCount = 1;                                         // link count is currently broken; set a default of one (itself)
      entry.uid = users.get(posixAttribs.owner().getName());       // use containsKey() if this causes a null pointer exception
      entry.gid = groups.get(posixAttribs.group().getName());      // same as above
      entry.atime = posixAttribs.lastAccessTime().toMillis();
      entry.ctime = posixAttribs.creationTime().toMillis();
      entry.mtime = posixAttribs.lastModifiedTime().toMillis();
      if(posixAttribs.isSymbolicLink())
        entry.link = Files.readSymbolicLink(directory).toString();
      else
        entry.link = null;
      entry.data = "";
      writeDatabase(entry, false, true);

      return FileVisitResult.CONTINUE;
    }
  }

  public static void readGroup()
  {
    try
    {
      BufferedReader groupFile = new BufferedReader(new FileReader("/etc/group"));
      String line = null;

      while((line = groupFile.readLine()) != null)
      {
        String[] entry = line.split(":");
        groups.put(entry[0], Integer.parseInt(entry[2])); // store group name associated with GID for lookup later
      }
      groupFile.close();
    }
    catch(IOException ioe)
    {
      System.out.println("Unable to read /etc/group.");
      System.out.println(ioe.getMessage());
    }
  }

  public static void readPasswd()
  {
    try
    {
      BufferedReader passwdFile = new BufferedReader(new FileReader("/etc/passwd"));
      String line = null;

      while((line = passwdFile.readLine()) != null)
      {
        String[] entry = line.split(":");
        users.put(entry[0], Integer.parseInt(entry[2])); // store username associated with UID for lookup later
      }
      passwdFile.close();
    }
    catch(IOException ioe)
    {
      System.out.println("Unable to read /etc/passwd.");
      System.out.println(ioe.getMessage());
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
      if(passedFile.toString().equals("/dev/core") || passedFile.toString().equals("/proc/kcore")) // problematic 128T file on 64-bit systems
        data = "";
      else
      {
        targetFile = new FileInputStream(file);
        byte[] content = new byte[(int)file.length()]; // may cause issues for large files
        targetFile.read(content);
        data = new String(content);
        targetFile.close();
      }
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

  public static byte determineType(Path file, PosixFileAttributes posixAttribs)
  {
    byte type = 5;

    if(posixAttribs.isRegularFile())
      type = 5;
    else if(posixAttribs.isDirectory())
      type = 2;
    else if(posixAttribs.isSymbolicLink())
      type = 4;
    else if(posixAttribs.isOther())
    {
      try
      {
        Process process = new ProcessBuilder("/bin/ls", "-la", file.toString()).start();
        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        line = bufferedReader.readLine();
        if(line.charAt(0) == 'b')
          type = 0; // block
        else if(line.charAt(0) == 'c')
          type = 1; // character
        else if(line.charAt(0) == 'p')
          type = 3; // pipe/FIFO
        else if(line.charAt(0) == 's')
          type = 6; // socket
      }
      catch(IOException ioe)
      {
        System.out.println("Unable to run process to determine file type.");
        System.out.println(ioe.getMessage());
      }
    }

    return type;
  }

  public static int calculateOctalPerms(String symbolicPerms)
  {
    String octalPerms = "";
    String subPerms = null;
    int start = 0;
    int end = 3;

    if(symbolicPerms != null && symbolicPerms.length() == 9)
    {
      for(int i = 0; i < 3; i++) // 3 chunks (user, group, other)
      {
        subPerms = symbolicPerms.substring(start, end);
        if(subPerms.equals("---"))
          octalPerms += "0";
        else if(subPerms.equals("r--"))
          octalPerms += "4";
        else if(subPerms.equals("-w-"))
          octalPerms += "2";
        else if(subPerms.equals("--x"))
          octalPerms += "1";
        else if(subPerms.equals("rw-"))
          octalPerms += "6";
        else if(subPerms.equals("r-x"))
          octalPerms += "5";
        else if(subPerms.equals("-wx"))
          octalPerms += "3";
        else if(subPerms.equals("rwx"))
          octalPerms += "7";
        else
          octalPerms += "0";
        start += 3;
        end += 3;
      }
    }
    else
      octalPerms = "000";

    int result = 0;
    try
    {
      result = Integer.parseInt(octalPerms);
    }
    catch(NumberFormatException nfe)
    {
      result = 000;
    }

    return result;
  }

  public static void connectDatabase()
  {
    try
    {
      System.setProperty("derby.system.home", System.getProperty("user.dir"));
      //System.setProperty("derby.stream.error.file", "log" + System.getProperty("file.separator") + "database.log");
      System.setProperty("derby.language.logStatementText", "false"); // set to true for increased verbosity
      connection = DriverManager.getConnection(protocol + databaseName + ";create=true", properties);
      System.out.println("Connected to database successfully.");
      connection.setAutoCommit(false);
      preparedStatement = connection.prepareStatement("INSERT INTO filesystem (path, name, inode, type, mode, linkcount, uid, gid, atime, " +
                                                      "ctime, mtime, link, bindata, txtdata) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }
    catch(SQLException sqle)
    {
      System.err.println("Critical: Unable to connect to database.");
      System.err.println(sqle.getMessage());
      if(connection == null)
        System.exit(1);
    }
  }

  public static void disconnectDatabase()
  {
    try
    {
      connection.close();
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }
    catch(SQLException sqle)
    {
      System.err.println("Critical: Unable to disconnect from database.");
      System.err.println(sqle.getMessage());
    }
  }

  public static void shutdownDatabase()
  {
    try
    {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }
    catch(SQLException sqle)
    {
      if(((sqle.getErrorCode() == 50000) && ("XJ015".equals(sqle.getSQLState()))))
      {
        System.out.println("Database was shut down properly.");
      }
      else
      {
        System.err.println("Critical: Database could not be shut down properly.");
        System.err.println(sqle.getMessage());
      }
    }
  }

  public static void writeDatabase(Record file, boolean isBinary, boolean isDirectory)
  {
    Blob blob = null;

    try
    {
      preparedStatement.setString(1, file.path);
      preparedStatement.setString(2, file.name);
      preparedStatement.setLong(3, file.inode);
      preparedStatement.setByte(4, file.type);
      preparedStatement.setInt(5, file.mode);
      preparedStatement.setInt(6, file.linkCount);
      preparedStatement.setInt(7, file.uid);
      preparedStatement.setInt(8, file.gid);
      preparedStatement.setLong(9, file.atime);     // a/c/mtime may need setTimestamp()
      preparedStatement.setLong(10, file.ctime);
      preparedStatement.setLong(11, file.mtime);
      preparedStatement.setString(12, file.link);
      if(isBinary)
      {
        blob = connection.createBlob();
        blob.setBytes(1, file.data.getBytes());
        preparedStatement.setBlob(13, blob);
        //preparedStatement.setString(13, file.data); // this may need setBlob()
        preparedStatement.setNull(14, Types.CLOB);
      }
      else if(isDirectory)
      {
        preparedStatement.setNull(13, Types.BLOB);
        preparedStatement.setNull(14, Types.CLOB);
      }
      else // is text
      {
        preparedStatement.setNull(13, Types.BLOB);
        preparedStatement.setString(14, file.data); // this may need setClob()
      }
      preparedStatement.executeUpdate();
      connection.commit();
    }
    catch(SQLException sqle)
    {
      System.err.println("Critical: Unable to write to database.");
      System.err.println(sqle.getMessage());
      if(connection == null)
        System.exit(1);
    }
  }

  public static void showUsage()
  {
    System.out.println("CloneFilesystem Usage");
    System.out.println("=====================");
    System.out.println("-c\tCreate clone");
    System.out.println("-h\tDisplay help\n");
  }
}
