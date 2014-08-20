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

class Limits
{
  public static final int MIN_PORT = 0;
  public static final int MAX_PORT = 65535;

  public static final byte MIN_PROCESSORS = 1;
  public static final byte MAX_PROCESSORS = 16;

  public static final int MIN_MEMORY = 1024;
  public static final int MAX_MEMORY = 32768;

  public static final short MAX_HOST_NAME_LENGTH = 255; // returned by "getconf HOST_NAME_MAX"

  public static final byte MAX_USER_NAME_LENGTH = 32;
  public static final byte MAX_PASSWORD_LENGTH = 32;
}
