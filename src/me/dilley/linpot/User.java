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

class User
{
  public static int SECONDS_IN_A_DAY = 86400;

  private String userName;
  private int uid;              // user ID
  private int gid;              // group ID
  private String gecos;         // real name/comment
  private String homeDirectory;
  private String shell;
  private String password;
  private String passwordHash;
  private int lastChanged;      // days elapsed since 01/01/1970 when password was last changed

  // For use with useradd
  public User(String userName, int uid, int gid, String gecos, String homeDirectory, String shell)
  {
    this.userName = userName;
    this.uid = uid;
    this.gid = gid;
    this.gecos = gecos;
    this.homeDirectory = homeDirectory;
    this.shell = shell;
    password = null;
    passwordHash = null;
    lastChanged = (int)(System.currentTimeMillis() / 1000 / SECONDS_IN_A_DAY);
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public int getUid()
  {
    return uid;
  }

  public void setUid(int uid)
  {
    this.uid = uid;
  }

  public int getGid()
  {
    return gid;
  }

  public void setGid(int gid)
  {
    this.gid = gid;
  }

  public String getGecos()
  {
    return gecos;
  }

  public void setGecos(String gecos)
  {
    this.gecos = gecos;
  }

  public String getHomeDirectory()
  {
    return homeDirectory;
  }

  public void setHomeDirectory(String homeDirectory)
  {
    this.homeDirectory = homeDirectory;
  }

  public String getShell()
  {
    return shell;
  }

  public void setShell(String shell)
  {
    this.shell = shell;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPasswordHash()
  {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash)
  {
    this.passwordHash = passwordHash;
  }

  public int getLastChanged()
  {
    return lastChanged;
  }

  public void setLastChanged(int lastChanged)
  {
    this.lastChanged = lastChanged;
  }
}
