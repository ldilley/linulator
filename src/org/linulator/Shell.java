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

import org.linulator.commands.*;

import java.util.HashMap;

class Shell
{
  private static HashMap<String, Command> hashMap = new HashMap<String, Command>();

  public static void populateCommands()
  {
    Uname uname = new Uname();
    hashMap.put("uname", uname);
  }

  public static String execute(String[] args)
  {
    String result = null;

    Command command = hashMap.get(args[0]);

    if(command != null)
      result = command.execute(args);

    return result;
  }
}
