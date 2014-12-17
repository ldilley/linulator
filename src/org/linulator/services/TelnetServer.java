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

import org.linulator.Log;
import org.linulator.Network;
import org.linulator.OperatingSystem;
import org.linulator.Shell;

public class TelnetServer implements Runnable
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
      String result = null;
      byte attempts = 0;
      char sigil = '$';
      input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
      ios = new DataInputStream(clientSocket.getInputStream());
      dos = new DataOutputStream(clientSocket.getOutputStream());

      Log.write(0, "TelnetServer: Connection received from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');

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

      Log.write(0, "TelnetServer: " + username + " has logged in from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');

      // ToDo: display motd and last login
      ios.skipBytes(3); // ignore telnet negotiation
      if(username.equals("root"))
        sigil = '#';
      while(true)
      {
        Network.send(output, username + "@" + OperatingSystem.getShortName() + sigil + " ", false);
        command = Network.receive(input);
        if(command == null || command.length() == 0 || command.trim().length() == 0)
          continue;
        if(command.trim().equals("exit") || command.trim().equals("logout"))
        {
          Log.write(0, "TelnetServer: " + username + " has logged out from " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + '.');
          break;
        }

        command = command.trim();
        String[] args = command.split("\\s+");
        result = Shell.execute(args);

        if(result == null || result.length() == 0)
          Network.send(output, "-bash: " + args[0] + ": command not found", true);
        else
          Network.send(output, result, true);
      }

      input.close();
      output.close();
      Log.write(0, "TelnetServer: Connection to " + clientSocket.getInetAddress().getHostAddress() + ':' + clientSocket.getPort() + " closed.");
    }
    catch(IOException ioe)
    {
      ioe.getMessage();
    }
  }
}
