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
import java.util.Random;

class ChargenServer implements Runnable
{
  public static final int LINE_SIZE = 72;
  public static final int MAX_UDP_SIZE = 512;
  public static final int MAX_UDP_BUFFER_SIZE = 518; // evenly divisible by LINE_SIZE + 2
  protected byte[] buffer = null;
  protected boolean isUdp = false;
  protected Socket clientSocket = null;
  protected DatagramPacket packet = null;
  protected DatagramSocket serverSocket = null;
  protected BufferedReader input = null;
  protected BufferedWriter output = null;

  // TCP
  public ChargenServer(Socket clientSocket)
  {
    isUdp = false;
    this.clientSocket = clientSocket;
  }

  // UDP
  public ChargenServer(DatagramSocket serverSocket, byte[] buffer, DatagramPacket packet)
  {
    isUdp = true;
    this.serverSocket = serverSocket;
    this.buffer = buffer;
    this.packet = packet;
  }

  public void rotate(char[] pattern, int offset)
  {
    int i = 0;
    int j = 0;

    for(i = offset; j < LINE_SIZE; j++)
    {
      if(i > 126) // 126 = '~' (tilde ASCII code)
        i = 32;   // reset to ' ' (space)

      pattern[j] = (char)i;
      i++;
    }

    // Append carriage return and newline
    pattern[LINE_SIZE] = '\r';
    pattern[LINE_SIZE + 1] = '\n';
  }

  // Returns random UDP buffer size per RFC (0 to 512 characters)
  public int populate(char[] pattern, char[] totalPattern, int offset)
  {
    int i = 0;
    int j = 0;
    Random random = new Random();

    rotate(pattern, offset); // initialize pattern starting with '!'
    offset++;                // proceed to next character in ASCII sequence

    // Initial array copy
    for(i = 0; i < (LINE_SIZE + 2); i++)
      totalPattern[i] = pattern[i];

    // Complete the copy
    while(i < MAX_UDP_BUFFER_SIZE)
    {
      rotate(pattern, offset);
      offset++;
      for(j = 0; j < (LINE_SIZE + 2); j++)
      {
        totalPattern[i] = pattern[j];
        i++;
      }
    }

    return random.nextInt(MAX_UDP_SIZE + 1); // rand(MAX_UDP_SIZE) is exclusive
  }

  public void run()
  {
    char[] pattern = new char[LINE_SIZE + 2]; // +2 for '\r' and '\n'
    int offset = 33;                          // ASCII character code -- starting with '!'

    try
    {
      if(!isUdp)
      {
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        Log.write(0, "ChargenServer: TCP connection received from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');

        rotate(pattern, offset); // initialize pattern starting with '!'

        while(clientSocket != null && !clientSocket.isClosed())
        {
           Network.send(output, new String(pattern), false);
           offset++;
           if(offset > 126)
             offset = 32;
           rotate(pattern, offset);
           //Thread.sleep(100);     // sleep for 1/10th of a second if this service causes performance issues later
        }

        input.close();
        output.close();
        Log.write(0, "ChargenServer: TCP connection to " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " closed.");
      }
      else
      {
        char[] totalPattern = new char[MAX_UDP_BUFFER_SIZE];
        Log.write(0, "ChargenServer: UDP packet received from " + packet.getAddress().getHostAddress() + ':' + packet.getPort() + '.');
        int patternSize = populate(pattern, totalPattern, offset);
        totalPattern[patternSize] = '\r';
        totalPattern[patternSize + 1] = '\n';
        packet.setData(new String(totalPattern).getBytes(), 0, (patternSize + 2)); // +2 for '\r' and '\n'
        Network.sendTo(serverSocket, packet);
      }
    }
    // InterruptedException is required for Thread.sleep()
    //catch(InterruptedException ie)
    //{
    //  ie.getMessage();
    //}
    catch(IOException ioe)
    {
      ioe.getMessage();
    }
  }
}
