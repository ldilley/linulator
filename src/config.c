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
#include "config.h"
#include "log.h"

void read_config(config *options)
{
  int i, comment;
  char line[128];
  char *result, *value;
  FILE *config_file;

  config_file=fopen("etc/linpot.conf", "r");

  if(config_file==NULL)
  {
    printf("failed. Unable to read linpot.conf.\n");
    exit(EXIT_FAILURE);
  }

  while(fgets(line, sizeof(line), config_file)!=NULL)
  {
    /* We do not care about blank lines and comments in the config file */
    if(line[0]=='\n' || line[0]=='\r' || line[0]=='#')
      continue;

    /* Trim leading whitespace from line to see if it is another comment */
    comment=0;
    for(i=0; i<strlen(line); i++)
    {
      if(line[i]==' ' || line[i]=='\t')
        continue;
      if(line[i]!='#')
        break;
      if(line[i]=='#')
      {
        comment=1;
        break;
      }
    }
    if(comment==1)
      continue;

    /* Populate configuration variables from config file */
    result=strtok(line, " \t");
    if(strcmp(result, "listen_address")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      copy_string(&options->listen_address, result, value);
    }
    else if(strcmp(result, "echo_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->echo_port=atoi(value);
    }
    else if(strcmp(result, "daytime_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->daytime_port=atoi(value);
    }
    else if(strcmp(result, "chargen_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->chargen_port=atoi(value);
    }
    else if(strcmp(result, "time_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->time_port=atoi(value);
    }
    else if(strcmp(result, "ftp_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->ftp_port=atoi(value);
    }
    else if(strcmp(result, "ssh_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->ssh_port=atoi(value);
    }
    else if(strcmp(result, "telnet_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->telnet_port=atoi(value);
    }
    else if(strcmp(result, "smtp_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->smtp_port=atoi(value);
    }
    else if(strcmp(result, "dns_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->dns_port=atoi(value);
    }
    else if(strcmp(result, "http_port")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->http_port=atoi(value);
    }
    else if(strcmp(result, "system_name")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      copy_string(&options->system_name, result, value);
    }
    else if(strcmp(result, "root_pass")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      copy_string(&options->root_pass, result, value);
    }
    else if(strcmp(result, "fake_distro")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      copy_string(&options->fake_distro, result, value);
    }
    else if(strcmp(result, "fake_mem")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->fake_mem=atoi(value);
    }
    else if(strcmp(result, "set_uid")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      copy_string(&options->set_uid, result, value);
    }
    else if(strcmp(result, "set_gid")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      copy_string(&options->set_gid, result, value);
    }
    else if(strcmp(result, "enable_logging")==0)
    {
      value=strtok(NULL, " \t");
      validate_config(result, value);
      options->enable_logging=atoi(value);
    }
    /* If anything else is detected in the file, it is irrelevant */
    else
    {
      /* Replace trailing newline or carriage return so they do not get appended in the error output below */
      for(i=0; i<strlen(result); i++)
      {
        if(result[i]=='\n' || result[i]=='\r')
          result[i]='\0';
      }
      printf("failed. Extraneous line in linpot.conf: \"%s\"\n", result);
      exit(EXIT_FAILURE);
    }
  }

  fclose(config_file);

  return;
}

void copy_string(char **config_member, char *option, char *value) /* Thanks to Artur Kuptel for helping with the ptr to ptr fix */
{
  if(value[strlen(value)-1]=='\n')
    value[strlen(value)-1]='\0';

  *config_member=malloc(strlen(value)+1); /* +1 for null terminator */

  if(config_member==NULL)
  {
    printf("Unable to allocate memory to hold %s option value: %s\n", option, value);
    exit(EXIT_FAILURE);
  }
  strcpy(*config_member, value);

  return;
}

void free_config(config *options)
{
  free(options->listen_address);
  free(options->system_name);
  free(options->root_pass);
  free(options->fake_distro);
  free(options->set_uid);
  free(options->set_gid);

  return;
}

void validate_config(char *option, char *value)
{
  int i;

  if(value==NULL || strlen(value)==0 || value[0]=='\n' || value[0]=='\r' || value[0]==' ' || value[0]=='\t' || value[0]=='#')
  {
    printf("failed. Invalid %s value in linpot.conf.\n", option);
    exit(EXIT_FAILURE);
  }

  /* Make sure that addresses are valid hostnames or IPv4 addresses */
  if(strcmp(option, "listen_address")==0)
  {
    /* perform checks here with gethostbyname, inet_pton(x, y, z), or inet_aton() */
  }

  /* Make sure that port numbers are within range */
  if(strcmp(option, "listen_port")==0 || strcmp(option, "ssl_port")==0)
  {
    if(atoi(value)<=0 || atoi(value)>65535)
    {
      /* Replace trailing newline or carriage return so they do not get appended in the error output below */
      for(i=0; i<strlen(value); i++)
      {
        if(value[i]=='\n' || value[i]=='\r')
          value[i]='\0';
      }
      printf("failed. Invalid network port defined for %s in linpot.conf: %s\n", option, value);
      exit(EXIT_FAILURE);
    }
  }

  return;
}
