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

package me.dilley.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.dilley.Log;
import me.dilley.Network;

public class DaytimeServer implements Runnable
{
  protected byte[] buffer = null;
  protected boolean isUdp = false;
  protected Socket clientSocket = null;
  protected DatagramPacket packet = null;
  protected DatagramSocket serverSocket = null;
  protected BufferedReader input = null;
  protected BufferedWriter output = null;
  protected SimpleDateFormat timestampFormat = null;
  protected Date currentTime = null;
  protected String timestamp = null;

  // TCP
  public DaytimeServer(Socket clientSocket)
  {
    isUdp = false;
    this.clientSocket = clientSocket;
  }

  // UDP
  public DaytimeServer(DatagramSocket serverSocket, byte[] buffer, DatagramPacket packet)
  {
    isUdp = true;
    this.serverSocket = serverSocket;
    this.buffer = buffer;
    this.packet = packet;
  }

  public void run()
  {
    timestampFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm:ss-zzz");
    currentTime = new Date();
    timestamp = timestampFormat.format(currentTime);

    try
    {
      if(!isUdp)
      {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        Log.write(0, "DaytimeServer: TCP connection received from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');
        Network.send(output, timestamp, true);
        input.close();
        output.close();
        Log.write(0, "DaytimeServer: TCP connection to " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " closed.");
      }
      else
      {
        Log.write(0, "DaytimeServer: UDP packet received from " + packet.getAddress().getHostAddress() + ':' + packet.getPort() + '.');
        buffer = timestamp.getBytes();
        packet.setData(buffer, 0, buffer.length);
        Network.sendTo(serverSocket, packet);
      }
    }
    catch(IOException ioe)
    {
      ioe.getMessage();
    }
  }
}
