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

package me.dilley;

public class OperatingSystem
{
  public static final String HARDWARE_ARCHITECTURE = "x86_64";
  public static final String OPERATING_SYSTEM = "GNU/Linux";
  public static final String KERNEL_RELEASE = "2.6.32-431.23.3.el6.x86_64";
  public static final String KERNEL_VERSION = "#1 SMP Thu Jul 31 17:20:51 UTC 2014";

  private static String hostName;
  private static String shortName;
  private static String domainName;
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

  public static synchronized String getShortName()
  {
    return shortName;
  }

  public static synchronized void setShortName(String newShortName)
  {
    shortName = newShortName;
  }

  public static synchronized String getDomainName()
  {
    return domainName;
  }

  public static synchronized void setDomainName(String newDomainName)
  {
    domainName = newDomainName;
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
