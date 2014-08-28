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

class Defaults
{
  public static final int DEFAULT_ECHO_PORT = 7;
  public static final int DEFAULT_DAYTIME_PORT = 13;
  public static final int DEFAULT_CHARGEN_PORT = 19;
  public static final int DEFAULT_TIME_PORT = 37;
  public static final int DEFAULT_FTP_PORT = 21;
  public static final int DEFAULT_SSH_PORT = 22;
  public static final int DEFAULT_TELNET_PORT = 23;
  public static final int DEFAULT_SMTP_PORT = 25;
  public static final int DEFAULT_DNS_PORT = 59;
  public static final int DEFAULT_HTTP_PORT = 80;
  public static final byte DEFAULT_PROCESSORS = 8;
  public static final int DEFAULT_MEMORY = 8192;
  public static final String DEFAULT_LISTEN_ADDRESS = "*";
  public static final String DEFAULT_HOST_NAME = "mirage";
  public static final String DEFAULT_DOMAIN_NAME = "spoof.dom";
  public static final String DEFAULT_ROOT_PASSWORD = "secret";
  public static final String DEFAULT_DISTRIBUTION = "centos5";
  public static final boolean DEFAULT_HIDE_CONSOLE = false;
  public static final boolean DEFAULT_DEBUG_MODE = true;
}
