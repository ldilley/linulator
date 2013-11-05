/*
 * $Id$
 * LinPot - A Linux honeypot
 * Copyright (C) 2013 Lloyd S. Dilley <lloyd@dilley.me>
 * http://www.devux.org/projects/linpot/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

#ifndef FILESYSTEM_H
#define FILESYSTEM_H

#include "types.h"

typedef struct
{
  char *device;
  char *mountpoint;
  ulong size;
  ulong available;
  ulong used;
} filesystem;

typedef struct
{
  char *name;
  ulong inode;
  ulong size;
  char *path;
  char *user;
  char *group;
  time atime;
  time ctime;
  time mtime;
  ushort perms;
  file *contents[];
} directory;

typedef struct
{
  char *name;
  ulong inode;
  ulong size;
  char *path;
  char *user;
  char *group;
  time atime;
  time ctime;
  time mtime;
  ushort perms;
} file;

#endif /* FILESYSTEM_H */
