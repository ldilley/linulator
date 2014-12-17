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

package org.linulator.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.linulator.Log;
import org.linulator.Network;
import org.linulator.OperatingSystem;

public class HttpServer implements Runnable
{
  public static final String version = "Apache/2.2.22";
  protected Socket clientSocket = null;
  protected BufferedReader input = null;
  protected BufferedWriter output = null;

  public HttpServer(Socket clientSocket)
  {
    this.clientSocket = clientSocket;
  }

  public String getCode(int code)
  {
    String response = null;
    SimpleDateFormat timestampFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    Date currentTime = new Date();
    String timestamp = timestampFormat.format(currentTime);

    switch(code)
    {
      case 404:
        break;
      case 500:
        response = "HTTP/1.1 500 Internal Server Error\r\n";
        response += "Date: " + timestamp + "\r\n";
        response += "Server: " + version + "\r\n";
        response += "Connection: close\r\n";
        response += "Content-Type: text/html; charset=iso-8859-1\r\n";
        response += "\r\n";
        response += "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n";
        response += "<html><head>\r\n";
        response += "<title>500 Internal Server Error</title>\r\n";
        response += "</head><body>\r\n";
        response += "<h1>Internal Server Error</h1>\r\n";
        response += "<p>The server encountered an internal error or misconfiguration and was unable to complete your request.<br />\r\n";
        response += "</p>\r\n";
        response += "<hr>\r\n";
        response += "<address>" + version + " Server at " + OperatingSystem.getHostName() + " Port " + clientSocket.getLocalPort() + "</address>\r\n";
        response += "</body></html>\r\n";
        break;
      default:
        // 400 bad request
        break;
    }

    return response;
  }

  public void run()
  {
    try
    {
      String request = null;
      input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

      Log.write(0, "HttpServer: Connection received from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');

      request = Network.receive(input);

      while(request == null || request.length() == 0)
      {
        request = Network.receive(input);
        continue;
      }

      //if(request.charAt(0) == ' ' || request.charAt(0) == '\t')
      //{
      //  Network.send(output, getCode(500), false); // send 500 instead of 400 for now
      //}

      request = request.replaceAll("\\s+$",""); // rtrim()
      String[] args = request.split("\\s+");

      // Nothing is implemented yet, so just send 500 for everything for now
      // ToDo: Log all requests
      switch(args[0])
      {
        case "GET":
          Network.send(output, getCode(500), false);
          break;
        case "PUT":
          Network.send(output, getCode(500), false);
          break;
        case "HEAD":
          Network.send(output, getCode(500), false);
          break;
        case "POST":
          Network.send(output, getCode(500), false);
          break;
        case "TRACE":
          Network.send(output, getCode(500), false);
          break;
        case "DELETE":
          Network.send(output, getCode(500), false);
          break;
        case "CONNECT":
          Network.send(output, getCode(500), false);
          break;
        case "OPTIONS":
          Network.send(output, getCode(500), false);
          break;
        default:
          Network.send(output, getCode(500), false);
          break;
      }

      input.close();
      output.close();
      Log.write(0, "HttpServer: Connection to " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " closed.");
    }
    catch(IOException ioe)
    {
      ioe.getMessage();
    }
  }
}
