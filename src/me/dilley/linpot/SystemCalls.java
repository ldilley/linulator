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

import java.util.Arrays;

/*
 * This class emulates Linux syscalls for the x86-64 architecture.
 * I understand that the method names below are not consistent with other methods in LinPot (nor do they follow
 * Sun's recommendations.) I am attempting to preserve the look of the Linux API. I *may* standardize the names
 * at some point. For example, "sys_write" might become "sysWrite" or simply "write".
 */

class SystemCalls
{
  public static String sys_call(int id, String[] args)
  {
    String returnValue = "";

    try
    {
      switch(id)
      {
        case 0:
          returnValue = Integer.toString(sys_read(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2])));
          break;
        case 1:
          returnValue = Integer.toString(sys_write(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2])));
          break;
        case 2:
          returnValue = Integer.toString(sys_open(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])));
          break;
        case 3:
          returnValue = Integer.toString(sys_close(Integer.parseInt(args[0])));
          break;
        default:
          Log.write(1, "Invalid system call: " + id);
          System.err.println("Warning: Invalid system call: " + id);
          break;
      }
    }
    catch(Exception e)
    {
      Log.write(1, "Unable to make system call: " + id);
      Log.write(1, "Arguments: " + Arrays.toString(args));
      Log.write(1, e.getMessage());
      System.err.println("Warning: Unable to make system call: " + id);
      System.err.println("Arguments: " + Arrays.toString(args));
      System.err.println(e.getMessage());
    }

    return returnValue;
  }

  // syscall placeholders for now
  public static int sys_read(int fileDescriptor, String buffer, int count)
  {
    return 0;
  }

  public static int sys_write(int fileDescriptor, String buffer, int count)
  {
    return 0;
  }

  public static int sys_open(String file, int flags, int mode)
  {
    return 0;
  }

  public static int sys_close(int fileDescriptor)
  {
    return 0;
  }

  /*
   * Unlike the standard sys_stat, this method takes an optional file descriptor
   * so that it can eliminate the need for sys_fstat and sys_lstat.
   */
  public static int sys_stat(int fileDescriptor, String file, StatBuffer statbuffer)
  {
    //if(!fileDescriptor == -1)
    // stat by fd if defined

    //if(isLink(file))
    // stat by link

    return 0;
  }

  // Resume work here... I got too tired adding all the error codes.
}
