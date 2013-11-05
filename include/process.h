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

#ifndef PROCESS_H
#define PROCESS_H

#include "time.h"
#include "types.h"

typedef struct
{
  ushort pid;
  ushort ppid;    /* parent PID */
  ushort tty;
  char *user;
  char *group;
  char *command;
  char stat[4];
  float cpu;
  float mem;
  uint vsz;       /* virtual memory size */
  uint rss;       /* resident set size */
  time start;     /* start time */
  time proc_time;
} process;

#endif /* PROCESS_H */
