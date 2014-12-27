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

// ToDo: This class needs to be thread safe
class File
{
  // File types
  public static final int BLOCK = 0;
  public static final int CHARACTER = 1;
  public static final int DIRECTORY = 2;
  public static final int FIFO = 3;
  public static final int LINK = 4;
  public static final int REGULAR = 5;
  public static final int SOCKET = 6;

  private String path;         // name of file prefixed with absolute path
  private String name;         // base name of file
  private long inode;          // unique ID
  private byte type;           // file type
  private int mode;            // octal permissions
  private int linkCount;       // number of hard links
  private int uid;             // user ID of owner
  private int gid;             // group ID of owner
  private long size;           // size of file in bytes
  private long atime;          // last access time
  private long ctime;          // create time
  private long mtime;          // last modify time
  private String link;         // link path
  private boolean isBlock;     // block device
  private boolean isCharacter; // character device
  private boolean isDirectory;
  private boolean isFifo;      // queue
  private boolean isLink;
  private boolean isRegular;
  private boolean isSocket;

  public File()
  {
    // default constructor
  }

  // For use with sys_open, touch, mkdir, etc.
  public File(String path, String name, long inode, int mode, int uid, int gid, long size, int type)
  {
    this.path = path;
    this.name = name;
    this.inode = inode;
    this.mode = mode;
    this.uid = uid;
    this.gid = gid;
    this.size = size;
    this.linkCount = 0;
    atime = System.currentTimeMillis() / 1000;
    ctime = atime;
    mtime = atime;
    isBlock = false;
    isCharacter = false;
    isDirectory = false;
    isFifo = false;
    isLink = false;
    isRegular = false;
    isSocket = false;

    switch(type)
    {
      case 0:
        isBlock = true;
        break;
      case 1:
        isCharacter = true;
        break;
      case 2:
        isDirectory = true;
        break;
      case 3:
        isFifo = true;
        break;
      case 4:
        isLink = true;
        break;
      case 5:
        isRegular = true;
        break;
      case 6:
        isSocket = true;
        break;
      default:
        isRegular = true;
        break;
    }
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getInode()
  {
    return inode;
  }

  public void setInode(long inode)
  {
    this.inode = inode;
  }

  public byte getType()
  {
    return type;
  }

  public void setType(byte type)
  {
    this.type = type;
  }

  public int getMode()
  {
    return mode;
  }

  public void setMode(int mode)
  {
    this.mode = mode;
  }

  public int getLinkCount()
  {
    return this.linkCount;
  }

  public void setLinkCount(int linkCount)
  {
    this.linkCount = linkCount;
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

  public long getSize()
  {
    return size;
  }

  public void setSize(long size)
  {
    this.size = size;
  }

  public long getAtime()
  {
    return atime;
  }

  public void setAtime(long atime)
  {
    this.atime = atime;
  }

  public long getCtime()
  {
    return ctime;
  }

  public void setCtime(long ctime)
  {
    this.ctime = ctime;
  }

  public long getMtime()
  {
    return mtime;
  }

  public void setMtime(long mtime)
  {
    this.mtime = mtime;
  }

  public String getLink()
  {
    return link;
  }

  public void setLink(String link)
  {
    this.link = link;
  }

  public boolean isBlock()
  {
    return isBlock;
  }

  public boolean isCharacter()
  {
    return isCharacter;
  }

  public boolean isDirectory()
  {
    return isDirectory;
  }

  public boolean isFifo()
  {
    return isFifo;
  }

  public boolean isLink()
  {
    return isLink;
  }

  public boolean isRegular()
  {
    return isRegular;
  }

  public boolean isSocket()
  {
    return isSocket;
  }
}
