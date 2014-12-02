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
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.TimeZone;

class TimeServer implements Runnable
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
  protected Calendar calendar = null;

  // TCP
  public TimeServer(Socket clientSocket)
  {
    isUdp = false;
    this.clientSocket = clientSocket;
  }

  // UDP
  public TimeServer(DatagramSocket serverSocket, byte[] buffer, DatagramPacket packet)
  {
    isUdp = true;
    this.serverSocket = serverSocket;
    this.buffer = buffer;
    this.packet = packet;
  }

  public void run()
  {
    calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Long long_timestamp = calendar.getTimeInMillis() / 1000L;
    int int_timestamp = long_timestamp.intValue();                   // need 32-bit value per RFC (FixMe before February 7, 2036!) :)
    buffer = ByteBuffer.allocate(4).putInt(int_timestamp).array();   // 32-bit aligned network byte order
    //String timestamp = new String(buffer);                         // for use with send() (TCP connections)

    try
    {
      if(!isUdp)
      {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        ios = new DataInputStream(clientSocket.getInputStream());
        dos = new DataOutputStream(clientSocket.getOutputStream());

        Log.write(0, "TimeServer: TCP connection received from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');
        //Network.send(output, timestamp, true);
        dos.writeInt(int_timestamp);             // writeInt() takes care of 4 byte big endian conversion
        input.close();
        output.close();
        Log.write(0, "TimeServer: TCP connection to " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " closed.");
      }
      else
      {
        Log.write(0, "TimeServer: UDP packet received from " + packet.getAddress().getHostAddress() + ':' + packet.getPort() + '.');
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
