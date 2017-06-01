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

package me.dilley.commands;

import me.dilley.OperatingSystem;

public class Uname extends Command
{
  protected String result;

  public String execute(String[] args)
  {
    result = null;

    if(args.length == 1)
      result = "Linux";

    if(args.length > 1)
    {
      if(findArg(args, "-a") || findArg(args, "--all"))
      {
        result = "Linux " + OperatingSystem.getShortName() + " " + OperatingSystem.KERNEL_RELEASE;
        result += " " + OperatingSystem.KERNEL_VERSION + " " + OperatingSystem.HARDWARE_ARCHITECTURE;
        result += " " + OperatingSystem.HARDWARE_ARCHITECTURE + " " + OperatingSystem.HARDWARE_ARCHITECTURE;
        result += " " + OperatingSystem.OPERATING_SYSTEM;
        return result;
      }
      if(findArg(args, "--help"))
      {
        result = "Usage: uname [OPTION]...\n";
        result += "Print certain system information.  With no OPTION, same as -s.\n\n";
        result += "  -a, --all                print all information, in the following order,\n";
        result += "                             except omit -p and -i if unknown:\n";
        result += "  -s, --kernel-name        print the kernel name\n";
        result += "  -n, --nodename           print the network node hostname\n";
        result += "  -r, --kernel-release     print the kernel release\n";
        result += "  -v, --kernel-version     print the kernel version\n";
        result += "  -m, --machine            print the machine hardware name\n";
        result += "  -p, --processor          print the processor type or \"unknown\"\n";
        result += "  -i, --hardware-platform  print the hardware platform or \"unknown\"\n";
        result += "  -o, --operating-system   print the operating system\n";
        result += "      --help     display this help and exit\n";
        result += "      --version  output version information and exit\n\n";
        result += "Report uname bugs to bug-coreutils@gnu.org\n";
        result += "GNU coreutils home page: <http://www.gnu.org/software/coreutils/>\n";
        result += "General help using GNU software: <http://www.gnu.org/gethelp/>\n";
        result += "For complete documentation, run: info coreutils 'uname invocation'\n";
        return result;
      }
      if(findArg(args, "--version"))
      {
        result = "uname (GNU coreutils) 8.4\nCopyright (C) 2010 Free Software Foundation, Inc.\n";
        result += "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
        result += "This is free software: you are free to change and redistribute it.\n";
        result += "There is NO WARRANTY, to the extent permitted by law.\n\n";
        result += "Written by David MacKenzie.";
        return result;
      }
      if(findArg(args, "-s") || findArg(args, "--kernel-name"))
        result = "Linux";
      if(findArg(args, "-n") || findArg(args, "--nodename"))
      {
        if(result != null)
          result += " " + OperatingSystem.getShortName();
        else
          result = OperatingSystem.getShortName();
      }
      if(findArg(args, "-r") || findArg(args, "--kernel-release"))
      {
        if(result != null)
          result += " " + OperatingSystem.KERNEL_RELEASE;
        else
          result = OperatingSystem.KERNEL_RELEASE;
      }
      if(findArg(args, "-v") || findArg(args, "--kernel-version"))
      {
        if(result != null)
          result += " " + OperatingSystem.KERNEL_VERSION;
        else
          result = OperatingSystem.KERNEL_VERSION;
      }
      if(findArg(args, "-m") || findArg(args, "--machine"))
      {
        if(result != null)
          result += " " + OperatingSystem.HARDWARE_ARCHITECTURE;
        else
          result = OperatingSystem.HARDWARE_ARCHITECTURE;
      }
      if(findArg(args, "-p") || findArg(args, "--processor"))
      {
        if(result != null)
          result += " " + OperatingSystem.HARDWARE_ARCHITECTURE;
        else
          result = OperatingSystem.HARDWARE_ARCHITECTURE;
      }
      if(findArg(args, "-i") || findArg(args, "--hardware-platform"))
      {
        if(result != null)
          result += " " + OperatingSystem.HARDWARE_ARCHITECTURE;
        else
          result = OperatingSystem.HARDWARE_ARCHITECTURE;
      }
      if(findArg(args, "-o") || findArg(args, "--operating-system"))
      {
        if(result != null)
          result += " " + OperatingSystem.OPERATING_SYSTEM;
        else
          result = OperatingSystem.OPERATING_SYSTEM;
      }
    }

    return result;
  }

  public boolean findArg(String[] args, String arg)
  {
    boolean hasArg = false;

    for(int i = 1; i < args.length; i++)
    {
      if(args[i].equals(arg))
      {
        hasArg = true;
        break;
      }
      if((args[i].length() > 2) && (args[i].charAt(0) == '-'))
      {
        for(int j = 1; j < args[i].length(); j++)
        {
          if(args[i].charAt(j) == arg.charAt(1))
          {
            hasArg = true;
            break;
          }
        }
      }
    }

    return hasArg;
  }
}
