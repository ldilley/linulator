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
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

class EchoServer implements Runnable
{
  protected byte[] buffer = null;
  protected boolean isUdp = false;
  protected Socket clientSocket = null;
  protected DatagramPacket packet = null;
  protected DatagramSocket serverSocket = null;
  protected BufferedReader input = null;
  protected BufferedWriter output = null;
  protected DataInputStream ios = null;   // for raw data
  protected DataOutputStream dos = null;  // for raw data

  // TCP
  public EchoServer(Socket clientSocket)
  {
    isUdp = false;
    this.clientSocket = clientSocket;
  }

  // UDP
  public EchoServer(DatagramSocket serverSocket, byte[] buffer, DatagramPacket packet)
  {
    isUdp = true;
    this.serverSocket = serverSocket;
    this.buffer = buffer;
    this.packet = packet;
  }

  public void run()
  {
    try
    {
      if(!isUdp)
      {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        Log.write(0, "EchoServer: TCP connection received from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');

        while(clientSocket != null && !clientSocket.isClosed())
        {
          String message = Network.receive(input);
          if(message == null) // connection lost
            break;
          Network.send(output, message, true);
        }

        input.close();
        output.close();
        Log.write(0, "EchoServer: TCP connection to " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " closed.");
      }
      else
      {
        Log.write(0, "EchoServer: UDP packet received from " + packet.getAddress().getHostAddress() + ':' + packet.getPort() + '.');
        Network.sendTo(serverSocket, packet); // send back initial echo from caller

        while(true)
        {
          Network.receiveFrom(serverSocket, packet);
          Network.sendTo(serverSocket, packet);
        }
      }
    }
    catch(IOException ioe)
    {
      ioe.getMessage();
    }
  }
}
