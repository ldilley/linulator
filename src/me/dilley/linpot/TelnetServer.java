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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;

class TelnetServer implements Runnable
{
  protected Socket clientSocket = null;
  protected BufferedReader input = null;
  protected BufferedWriter output = null;
  protected DataInputStream ios = null;  // for raw data
  protected DataOutputStream dos = null; // for raw data

  public final byte[] disableEcho = {(byte)0xFF, (byte)0xFB, (byte)0x01}; // IAC WILL ECHO
  public final byte[] enableEcho = {(byte)0xFF, (byte)0xFC, (byte)0x01};  // IAC WONT ECHO

  public TelnetServer(Socket clientSocket)
  {
    this.clientSocket = clientSocket;
  }

  public void run()
  {
    try
    {
      boolean isAuthenticated = false;
      String username = null;
      String password = null;
      String command = null;
      byte attempts = 0;
      input=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      output=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      ios = new DataInputStream(clientSocket.getInputStream());
      dos = new DataOutputStream(clientSocket.getOutputStream());

      // ToDo: Actually validate credentials later
      while(!isAuthenticated)
      {
        Network.send(output, "Login: ", false);
        username = Network.receive(input);
        if(username == null || username.length() == 0)
          continue;
        Network.send(output, "Password: ", false);
        dos.write(disableEcho, 0, disableEcho.length);
        password = Network.receive(input);
        dos.write(enableEcho, 0, enableEcho.length);
        Network.send(output, "", true);
        if(password == null || password.length() == 0)
          continue;
        isAuthenticated = true;
      }

      // ToDo: display motd and last login
      ios.skipBytes(3); // ignore telnet negotiation
      while(true)
      {
        Network.send(output, username + "@" + OperatingSystem.getHostName() + "$ ", false);
        command = Network.receive(input);
        if(command == null || command.length() == 0 || command.trim().length() == 0)
          continue;
        if(command.trim().equals("exit") || command.trim().equals("logout"))
          break;
      }

      input.close();
      output.close();
    }
    catch(IOException ioe)
    {
      ioe.getMessage();
    }
  }
}
