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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class TcpServer implements Runnable
{
  protected ServerSocket serverSocket = null;
  protected boolean isStopped = false;
  protected Thread runningThread = null;
  protected int port = 0;
  protected String service = null;

  public TcpServer(int port, String service)
  {
    this.port = port;
    this.service = service;
  }

  public void run()
  {
    synchronized(this)
    {
      this.runningThread = Thread.currentThread();
    }

    try
    {
      this.serverSocket = new ServerSocket(this.port);
    }
    catch(IOException ioe)
    {
      Log.write(2, "TCP server unable to listen on port: " + this.port);
      Log.write(2, ioe.getMessage());
      System.err.println("Critical: TCP server unable to listen on port: " + this.port);
      System.err.println(ioe.getMessage());
    }

    while(!isStopped)
    {
      Socket clientSocket = null;

      try
      {
        clientSocket = this.serverSocket.accept();
      }
      catch(IOException ioe)
      {
        if(isStopped())
        {
          Log.write(2, "TCP server stopped.");
          Log.write(2, ioe.getMessage());
          System.err.println("Critical: TCP server stopped.");
          System.err.println(ioe.getMessage());
          return;
        }
        Log.write(1, "Error accepting client connection:");
        Log.write(1, ioe.getMessage());
      }

      if(this.service.equals("telnet"))
        new Thread(new TelnetServer(clientSocket)).start();
    }

    Log.write(0, "TCP server stopped.");
  }

  private synchronized boolean isStopped()
  {
    return this.isStopped;
  }

  public synchronized void stop()
  {
    this.isStopped = true;

    try
    {
      this.serverSocket.close();
    }
    catch(IOException ioe)
    {
      Log.write(2, "Unable to close TCP server.");
      Log.write(2, ioe.getMessage());
    }
  }
}
