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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;

class Network
{
  public static String receive(BufferedReader input)
  {
    String data = null;

    try
    {
      data = input.readLine();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }

    return data;
  }

  public static int send(BufferedWriter output, String data, boolean isSeparate)
  {
    int bytesSent = 0;

    try
    {
      output.write(data);
      if(isSeparate)
      {
        output.newLine();
        bytesSent = data.length() + System.getProperty("line.separator").length();
      }
      else
      {
        bytesSent = data.length();
      }
      output.flush();
    }
    catch(SocketException se)
    {
      se.getMessage();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }

    return bytesSent;
  }
}
