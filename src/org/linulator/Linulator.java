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

package org.linulator;

import java.util.InputMismatchException;
import java.util.Scanner;

class Linulator
{
  public static final String VERSION = "Linulator v0.1";
  static Scanner scanner = null;
  static String input = null;
  static int option = 0;

  public static void main(String[] args)
  {
    System.out.println(VERSION);
    System.out.println("Enforcing security policy...");
    System.setProperty("java.security.policy", "cfg${file.separator}linulator.policy");
    SecurityManager securityManager = new SecurityManager();
    System.setSecurityManager(securityManager);
    //securityManager.checkExec("/bin/ls"); // prove we cannot execute commands on the host OS
    Log.write(0, VERSION);
    Log.write(0, "Security policy enforced.");
    System.out.println("Parsing configuration file...");
    Log.write(0, "Parsing configuration file...");
    Config config = new Config();
    config.parseConfig();
    System.out.println("Setting hostname...");
    Log.write(0, "Setting hostname...");
    OperatingSystem.setHostName(config.getHostName());
    OperatingSystem.setShortName(config.getShortName());
    OperatingSystem.setDomainName(config.getDomainName());
    System.out.println("Populating commands...");
    Log.write(0, "Populating commands...");
    Shell.populateCommands();
    // ToDo: Load filesystem data from Derby database
    Database.connect();
    if(config.getTelnetPort() != 0)
    {
      System.out.println("Starting telnet server...");
      Log.write(0, "Starting telnet server...");
      TcpServer telnetServer = new TcpServer(config.getListenAddress(), config.getTelnetPort(), "telnet");
      new Thread(telnetServer).start();
    }

    System.out.println("Linulator started successfully.");
    Log.write(0, "Linulator started successfully.");

    while(true)
    {
      try
      {
        System.out.println("\nAdministrative Console");
        System.out.println("======================\n");
        System.out.println("1.) Open console");
        System.out.println("2.) View logs");
        System.out.println("3.) Freeze environment");
        System.out.println("4.) Show configuration");
        System.out.println("5.) Shutdown Linulator\n");
        System.out.print("> ");

        scanner = new Scanner(System.in);
        input = scanner.nextLine();

        if(input == null || input.trim().length() == 0)
          continue;

        input = input.trim();
        option = Integer.parseInt(input);
      }
      catch(NumberFormatException nfe)
      {
        System.out.println("Invalid option");
        continue;
      }
      catch(InputMismatchException ime)
      {
        System.out.println("Invalid option");
        continue;
      }

      switch(option)
      {
        case 1:
          openConsole();
          break;
        case 2:
          Log.viewLog();
          break;
        case 3:
          System.out.println("Not implemented");
          break;
        case 4:
          config.showConfig();
          break;
        case 5:
          System.out.println("Shutting down...");
          Log.write(0, "Shutting down...");
          // ToDo: stop service threads and commit database changes
          Database.shutdown();
          System.exit(0);
          break;
        default:
          System.out.println("Invalid option");
          break;
      }
    }
  }

  public static void openConsole()
  {
    Scanner scanner = null;
    String command = null;
    String result = null;

    while(true)
    {
      scanner = new Scanner(System.in);
      System.out.print("root@" + OperatingSystem.getShortName() + "# ");
      command = scanner.nextLine();

      if(command == null || command.length() == 0)
        continue;
      if(command.trim().equals("exit") || command.trim().equals("logout"))
        break;

      command = command.trim();
      String[] args = command.split("\\s+");
      result = Shell.execute(args);

      if(result == null || result.length() == 0)
        System.out.println("-bash: " + args[0] + ": command not found");
      else
        System.out.println(result);
    }
  }
}
