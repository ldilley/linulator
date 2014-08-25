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

class OperatingSystem
{
  private static String hostName;
  private static int nextFreeUid; // next available user ID
  private static int nextFreeGid; // next available group ID
  private static int nextFreePid; // next available process ID

  public static synchronized String getHostName()
  {
    return hostName;
  }

  public static synchronized void setHostName(String newHostName)
  {
    hostName = newHostName;
  }

  public static synchronized int getNextFreeUid()
  {
    return nextFreeUid;
  }

  public static synchronized int assignNextFreeUid()
  {
    return nextFreeUid++;
  }

  public static synchronized void setNextFreeUid(int uid)
  {
    nextFreeUid = uid;
  }

  public static synchronized int getNextFreeGid()
  {
    return nextFreeGid;
  }

  public static synchronized int assignNextFreeGid()
  {
    return nextFreeGid++;
  }

  public static synchronized void setNextFreeGid(int gid)
  {
    nextFreeGid = gid;
  }

  public static synchronized int getNextFreePid()
  {
    return nextFreePid;
  }

  public static synchronized int assignNextFreePid()
  {
    return nextFreePid++;
  }

  public static synchronized void setNextFreePid(int pid)
  {
    nextFreePid = pid;
  }
}
