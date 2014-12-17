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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Log
{
  public static final byte COLS = 80;
  public static final byte ROWS = 24;

  public static void write(int severity, String message)
  {
    SimpleDateFormat timestampFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date currentTime = new Date();
    String timestamp = timestampFormat.format(currentTime);
    String logEntry = "";
    FileWriter logFile;

    switch(severity)
    {
      case 0:
        logEntry = timestamp + " INFO: " + message + '\n';
        break;
      case 1:
        logEntry = timestamp + " WARN: " + message + '\n';
        break;
      case 2:
        logEntry = timestamp + " CRIT: " + message + '\n';
        break;
      case 3:
        logEntry = timestamp + " DBUG: " + message + '\n';
        break;
      default:
        logEntry = timestamp + " INFO: " + message + '\n';
        break;
    }

    try
    {
      logFile = new FileWriter("log" + System.getProperty("file.separator") + "linulator.log", true);
      logFile.write(logEntry);
      logFile.close();
    }
    catch(IOException ioe)
    {
      System.err.println("Unable to write to linulator.log file:");
      System.err.println(ioe.getMessage());
      System.exit(1);
    }
  }

  public static void viewLog()
  {
    BufferedReader logFile;
    Scanner scanner = null;
    String line;
    int rowCount = 0;

    try
    {
      logFile = new BufferedReader(new FileReader("log" + System.getProperty("file.separator") + "linulator.log"));
      while((line = logFile.readLine()) != null)
      {
        if(rowCount >= ROWS)
        {
          scanner = new Scanner(System.in);
          scanner.nextLine();
          rowCount = 0;
        }
        System.out.println(line);
        rowCount += 1;
      }
      logFile.close();
    }
    catch(IOException ioe)
    {
      System.err.println("Unable to read linulator.log file:");
      System.err.println(ioe.getMessage());
    }
  }
}
