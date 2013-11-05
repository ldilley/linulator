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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "log.h"
#include "version.h"

void write_log(ushort type, char *log_entry)
{
  int i=0;
  char *timestamp;
  time_t rawtime;
  struct tm *timedata;
  FILE *log_file;

  log_file=fopen("log/linpot.log", "a");

  if(log_file==NULL)
  {
    printf("Unable to open linpot.log for writing.\n");
    exit(EXIT_FAILURE);
  }

  time(&rawtime);
  timedata=localtime(&rawtime);
  timestamp=asctime(timedata);

  /* Remove newline from timestamp since we want our log entry on the same line */
  for(i=0; i<strlen(timestamp); i++)
  {
    if(timestamp[i]=='\n')
      timestamp[i]='\0';
  }

  /*
   * Message types:
   * 1.) INFO
   * 2.) AUTH
   * 3.) WARN
   * 4.) CRIT
   */
  switch(type)
  {
    case 1:
      fprintf(log_file, "[%s] INFO: %s\n", timestamp, log_entry);
      break;
    case 2:
      fprintf(log_file, "[%s] AUTH: %s\n", timestamp, log_entry);
      break;
    case 3:
      fprintf(log_file, "[%s] WARN: %s\n", timestamp, log_entry);
      break;
    case 4:
      fprintf(log_file, "[%s] CRIT: %s\n", timestamp, log_entry);
      break;
    default:
      fprintf(log_file, "[%s] INFO: %s\n", timestamp, log_entry);
      break;
  }

  fclose(log_file);

  return;
}
