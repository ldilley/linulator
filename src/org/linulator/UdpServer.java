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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

class UdpServer implements Runnable
{
  protected DatagramSocket serverSocket = null;
  protected boolean isStopped = false;
  protected Thread runningThread = null;
  protected String address = null;
  protected int port = 0;
  protected String service = null;

  public UdpServer(String address, int port, String service)
  {
    this.address = address;
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
      if(this.address.equals("*"))
        this.serverSocket = new DatagramSocket(this.port);
      else
        this.serverSocket = new DatagramSocket(this.port, InetAddress.getByName(this.address));
    }
    catch(IOException ioe)
    {
      Log.write(2, this.service + " server unable to listen on port: " + this.port);
      Log.write(2, ioe.getMessage());
      System.err.println("Critical: " + this.service + " server unable to listen on port: " + this.port);
      System.err.println(ioe.getMessage());
      System.exit(1);
    }

    while(!isStopped)
    {
      byte[] buffer = null;
      DatagramPacket packet = null;

      try
      {
        buffer = new byte[Limits.MAX_SMALL_SERVER_PACKET_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);
        this.serverSocket.receive(packet);
      }
      catch(IOException ioe)
      {
        if(isStopped())
        {
          Log.write(2, this.service + " server stopped.");
          Log.write(2, ioe.getMessage());
          System.err.println("Critical: " + this.service + " server stopped.");
          System.err.println(ioe.getMessage());
          return;
        }
        Log.write(1, "Error receiving packet:");
        Log.write(1, ioe.getMessage());
      }

      if(this.service.equals("echo"))
        new Thread(new EchoServer(serverSocket, buffer, packet)).start();
    }

    Log.write(0, this.service + " server stopped.");
  }

  private synchronized boolean isStopped()
  {
    return this.isStopped;
  }

  public synchronized void stop()
  {
    this.isStopped = true;

    if(this.serverSocket == null || this.serverSocket.isClosed())
      Log.write(2, "Unable to close " + this.service + " server.");
    else
      this.serverSocket.close();
  }
}
