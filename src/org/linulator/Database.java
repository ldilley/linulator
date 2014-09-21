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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// ToDo: This class needs to be thread safe
class Database
{
  private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
  private static String protocol = "jdbc:derby:";
  private static String databaseName = "linulator";
  private static Connection connection = null;
  private PreparedStatement preparedStatement;
  private Statement statement;
  private ResultSet resultSet = null;
  private static Properties properties = new Properties();

  public static void connect()
  {
    try
    {
      System.setProperty("derby.system.home", System.getProperty("user.dir"));
      System.setProperty("derby.stream.error.file", "log${file.separator}database.log");
      System.setProperty("derby.language.logStatementText", "true");
      connection = DriverManager.getConnection(protocol + databaseName + ";create=true", properties);
      Log.write(0, "Connected to database successfully.");
      System.out.println("Connected to database successfully.");
    }
    catch(SQLException sqle)
    {
      Log.write(2, "Unable to connect to database.");
      Log.write(2, sqle.getMessage());
      System.err.println("Critical: Unable to connect to database.");
      System.err.println(sqle.getMessage());
      if(connection == null)
        System.exit(1);
    }
  }

  public static void disconnect()
  {
    try
    {
      connection.close();
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }
    catch(SQLException sqle)
    {
      Log.write(2, "Unable to disconnect from database.");
      Log.write(2, sqle.getMessage());
      System.err.println("Critical: Unable to disconnect from database.");
      System.err.println(sqle.getMessage());
    }
  }

  public static void query(String query)
  {
    //
  }

  public static void shutdown()
  {
    try
    {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }
    catch(SQLException sqle)
    {
      if(((sqle.getErrorCode() == 50000) && ("XJ015".equals(sqle.getSQLState()))))
      {
        Log.write(0, "Database was shut down properly.");
        System.out.println("Database was shut down properly.");
      }
      else
      {
        Log.write(2, "Database could not be shut down properly.");
        Log.write(2, sqle.getMessage());
        System.err.println("Critical: Database could not be shut down properly.");
        System.err.println(sqle.getMessage());
      }
    }
  }
}
