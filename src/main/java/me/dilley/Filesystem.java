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

import java.math.BigInteger;

/* ToDo: This class needs to be thread safe since Linulator is a multi-user environment
         and more than one user may be modifying the filesystem. */
class Filesystem
{
  static long nextFreeInode; // gets set where it left off once filesystem data is loaded from Derby database
  static Node root;          // filesystem root (/)

  private String mountPoint;
  private BigInteger size;   // total filesystem size in bytes

  public Filesystem(Node root)
  {
    this.root = root;
  }

  public void add(File file)
  {
    root.add(file);
  }

  public String getMountPoint()
  {
    return mountPoint;
  }

  public void setMountPoint(String mountPoint)
  {
    this.mountPoint = mountPoint;
  }

  public BigInteger getSize()
  {
    return size;
  }

  public void setSize(BigInteger size)
  {
    this.size = size;
  }
}
