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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Scanner;
import java.util.Set;

// This class creates the initial system baseline from an existing Linux filesystem
class Baseline
{
  public static void main(String[] args)
  {
    if(!System.getProperty("os.name").equals("Linux"))
    {
      System.err.println("This must be run on a Linux system.");
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
      createBaseline();
    else if(args[0].equals("-h"))
      showUsage();
    else
    {
      System.err.println("Invalid argument.\n");
      showUsage();
      System.exit(1);
    }
  }

  public static void createBaseline()
  {
    Scanner scanner = new Scanner(System.in);
    String input = null;

    System.out.println("Warning: This process should only be performed on a fresh install to avoid importing personal data!");
    System.out.print("Type \"yes\" and press enter if you understand this and want to proceed: ");
    input = scanner.nextLine();

    if(!input.equalsIgnoreCase("yes"))
    {
      System.out.println("Process aborted per user request.");
      System.exit(0);
    }

    //importFiles("/");
    importFiles("/bin");
    importFiles("/boot");
    importFiles("/dev");
    importFiles("/etc");
    importFiles("/home");
    importFiles("/lib");
    importFiles("/lib64");
    importFiles("/media");
    importFiles("/mnt");
    importFiles("/opt");
    importFiles("/proc");
    importFiles("/root");
    importFiles("/sbin");
    importFiles("/selinux");
    importFiles("/srv");
    importFiles("/sys");
    importFiles("/tmp");
    importFiles("/usr");
    importFiles("/var");   
  }

  public static void showUsage()
  {
    System.out.println("Baseline Usage");
    System.out.println("==============");
    System.out.println("-c\tCreate system baseline");
    System.out.println("-h\tDisplays this help information\n");
  }

  public static void importFiles(String directory)
  {
    // Stopping here for now to play some video games...
    System.out.println("Not fully implemented yet.");
    System.exit(0);

    System.out.println("Importing " + directory + "...");

    try
    {
      Path path = Paths.get(directory);
      PosixFileAttributes attributes = Files.readAttributes(path, PosixFileAttributes.class);
      System.out.println(attributes.fileKey() + " " + attributes.creationTime());
      //Set<PosixFilePermission> set = Files.getPosixFilePermissions(path);
      //Set<PosixFileAttributes> set = Files.getPosixFileAttributes(path);
      //System.out.println(directory + ": " + PosixFilePermissions.toString(set));
      
    }
    catch(IOException ioe)
    {
      // keep going or exit?
    }
  }

  class File
  {
    // file attributes
  }
}
