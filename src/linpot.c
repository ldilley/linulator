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

#include <grp.h>       /* for getgrnam() */
#include <pwd.h>       /* for getpwnam() */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>    /* for fork(), setgid(), and setuid() */
#include <sys/types.h> /* for pid_t */
#include "config.h"
#include "linpot.h"
#include "log.h"
#include "network.h"
#include "version.h"

int main(int argc, char *argv[])
{
  int i;
  config options;
  pid_t pid;
  struct passwd *pwent;
  struct group *grpent;

  if(argc>1)
  {
    for(i=1; i<argc; i++)
    {
      if(strcmp(argv[i], "-h")==0)
      {
        print_usage(argv[0]);
        exit(EXIT_SUCCESS);
      }
      if(strcmp(argv[i], "-v")==0)
      {
        puts(VERSION);
        exit(EXIT_SUCCESS);
      }
      else
      {
        printf("%s: Invalid argument: %s\n", argv[0], argv[i]);
        exit(EXIT_FAILURE);
      }
    }
  }

  pid=fork();
  if(pid>=0)
  {
    if(pid==0)
    {
      /* write_pid(file); */
      write_log(1, VERSION);
      write_log(1, "Reading configuration file...");
      read_config(&options);
      if((pwent=getpwnam(options.set_uid))==NULL)
        perror("bleh");
      else
        setuid(pwent->pw_uid);  /* this call requires root to work */
      if((grpent=getgrnam(options.set_gid))==NULL)
        perror("bleh2");
      else
        setgid(grpent->gr_gid); /* this call also requires root to work */
      start_network(&options);
      free_config(&options);
    }
  }
  else
    puts("fork failed!");

  return 0;
}

void print_usage(char *prog_name)
{
  printf("Usage: %s [option]\n", prog_name);
  puts("Options:");
  puts("\t-h\tPrints this usage information and exits");
  puts("\t-v\tDisplay the program version and exit");
  return;
}
