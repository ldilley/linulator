/*
 * LinPot - A Linux honeypot
 * Copyright (C) 2014 Lloyd Dilley
 * http://www.dilley.me/
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

package me.dilley.linpot;

import java.util.InputMismatchException;
import java.util.Scanner;

class LinPot
{
  public static final String VERSION = "LinPot v0.1";
  static Scanner scanner = null;
  static int option = 0;

  public static void main(String[] args)
  {
    System.out.println(VERSION);
    System.out.println("Parsing configuration file...");
    Config config = new Config();
    config.parseConfig();
    System.out.println("Setting hostname...");
    OperatingSystem.setHostName(config.getHostName());
    // ToDo: Load filesystem data from Derby database
    if(config.getTelnetPort() != 0)
    {
      System.out.println("Starting telnet server...");
      TcpServer telnetServer = new TcpServer(config.getTelnetPort(), "telnet");
      new Thread(telnetServer).start();
    }

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
        System.out.println("5.) Shutdown LinPot\n");
        System.out.print("> ");

        scanner = new Scanner(System.in);
        option = scanner.nextInt();
      }
      catch(InputMismatchException ime)
      {
        System.out.println("Invalid option");
        continue;
      }

      switch(option)
      {
        case 1:
          System.out.println("Not implemented");
          break;
        case 2:
          System.out.println("Not implemented");
          break;
        case 3:
          System.out.println("Not implemented");
          break;
        case 4:
          config.showConfig();
          break;
        case 5:
          System.out.println("Shutting down...");
          // ToDo: stop service threads and commit database changes
          System.exit(0);
          break;
        default:
          System.out.println("Invalid option");
          break;
      }
    }
  }
}
