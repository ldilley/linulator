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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

class Config
{
  private String listenAddress;
  private int echoPort;
  private int daytimePort;
  private int chargenPort;
  private int timePort;
  private int ftpPort;
  private int sshPort;
  private int telnetPort;
  private int smtpPort;
  private int dnsPort;
  private int httpPort;
  private String hostName;
  private String shortName;
  private String domainName;
  private String rootPassword;
  private String fakeDistro;
  private byte fakeProcessors;
  private int fakeMemory;
  private boolean hideConsole;
  private boolean debugMode;

  public String getListenAddress()
  {
    return listenAddress;
  }

  public void setListenAddress(String listenAddress)
  {
    if(listenAddress.length() > 0)
      this.listenAddress = listenAddress;
    else
    {
      Log.write(0, "listen_address is not set. Listening on all available interfaces...");
      this.listenAddress = Defaults.DEFAULT_LISTEN_ADDRESS; // listen on all available interfaces
    }
  }

  public int getEchoPort()
  {
    return echoPort;
  }

  public void setEchoPort(String echoPort)
  {
    this.echoPort = validatePort(echoPort, this.echoPort, "echo", "echo_port");
  }

  public int getDaytimePort()
  {
    return daytimePort;
  }

  public void setDaytimePort(String daytimePort)
  {
    this.daytimePort = validatePort(daytimePort, this.daytimePort, "daytime", "daytime_port");
  }

  public int getChargenPort()
  {
    return chargenPort;
  }

  public void setChargenPort(String chargenPort)
  {
    this.chargenPort = validatePort(chargenPort, this.chargenPort, "chargen", "chargen_port");
  }

  public int getTimePort()
  {
    return timePort;
  }

  public void setTimePort(String timePort)
  {
    this.timePort = validatePort(timePort, this.timePort, "time", "time_port");
  }

  public int getFtpPort()
  {
    return ftpPort;
  }

  public void setFtpPort(String ftpPort)
  {
    this.ftpPort = validatePort(ftpPort, this.ftpPort, "FTP", "ftp_port");
  }

  public int getSshPort()
  {
    return sshPort;
  }

  public void setSshPort(String sshPort)
  {
    this.sshPort = validatePort(sshPort, this.sshPort, "SSH", "ssh_port");
  }

  public int getTelnetPort()
  {
    return telnetPort;
  }

  public void setTelnetPort(String telnetPort)
  {
    this.telnetPort = validatePort(telnetPort, this.telnetPort, "telnet", "telnet_port");
  }

  public int getSmtpPort()
  {
    return smtpPort;
  }

  public void setSmtpPort(String smtpPort)
  {
    this.smtpPort = validatePort(smtpPort, this.smtpPort, "SMTP", "smtp_port");
  }

  public int getDnsPort()
  {
    return dnsPort;
  }

  public void setDnsPort(String dnsPort)
  {
    this.dnsPort = validatePort(dnsPort, this.dnsPort, "DNS", "dns_port");
  }

  public int getHttpPort()
  {
    return httpPort;
  }

  public void setHttpPort(String httpPort)
  {
    this.httpPort = validatePort(httpPort, this.httpPort, "HTTP", "http_port");
  }

  public String getHostName()
  {
    return hostName;
  }

  public void setHostName(String hostName)
  {
    int domainIndex = 0;
    String domainName = "";

    if(hostName.length() > 0)
      this.hostName = hostName;
    else
    {
      Log.write(1, "host_name value is not set. Setting host name to: " + Defaults.DEFAULT_HOST_NAME);
      System.out.println("Warning: host_name value is not set. Setting host name to: " + Defaults.DEFAULT_HOST_NAME);
      this.hostName = Defaults.DEFAULT_HOST_NAME;
    }

    // Is it a valid host name?
    if(!this.hostName.matches("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$") || this.hostName.length() > Limits.MAX_HOST_NAME_LENGTH)
    {
      Log.write(1, "host_name value is invalid. Setting host name to: " + Defaults.DEFAULT_HOST_NAME);
      System.out.println("Warning: host_name value is invalid. Setting host name to: " + Defaults.DEFAULT_HOST_NAME);
      this.hostName = Defaults.DEFAULT_HOST_NAME;
    }

    domainIndex = this.hostName.indexOf('.');
    if(domainIndex > 0)
    {
      setShortName(this.hostName.substring(0, domainIndex));
      domainName = this.hostName.substring(domainIndex + 1);
    }
    else
      setShortName(this.hostName);

    setDomainName(domainName);
  }

  public String getShortName()
  {
    return shortName;
  }

  public void setShortName(String shortName)
  {
    this.shortName = shortName;
  }

  public String getDomainName()
  {
    return domainName;
  }

  public void setDomainName(String domainName)
  {
    this.domainName = domainName;
    // domainName is already set to empty in validateOption()
    //if(this.domainName.length() == 0)
    //{
    //  Log.write(1, "domain_name value is not set. Setting domain name to: " + Defaults.DEFAULT_DOMAIN_NAME);
    //  System.out.println("Warning: domain_name value is not set. Setting domain name to: " + Defaults.DEFAULT_DOMAIN_NAME);
    //  this.domainName = Defaults.DEFAULT_DOMAIN_NAME;
    //}
  }

  public String getRootPassword()
  {
    return rootPassword;
  }

  public String getMaskedRootPassword()
  {
    String maskedRootPassword = "";

    for(byte b = 0; b < rootPassword.length(); b++)
      maskedRootPassword += '*';

    return maskedRootPassword;
  }

  public void setRootPassword(String rootPassword)
  {
    if(rootPassword.length() > 0 || rootPassword.length() < Limits.MAX_PASSWORD_LENGTH)
      this.rootPassword = rootPassword;
    else
    {
      Log.write(1, "root_password value is invalid. Setting root password to: " + Defaults.DEFAULT_ROOT_PASSWORD);
      System.out.println("Warning: root_password value is invalid. Setting root password to: " + Defaults.DEFAULT_ROOT_PASSWORD);
      this.rootPassword = Defaults.DEFAULT_ROOT_PASSWORD;
    }
  }

  public String getFakeDistro()
  {
    return fakeDistro;
  }

  public void setFakeDistro(String fakeDistro)
  {
    if(fakeDistro.length() > 0)
      this.fakeDistro = fakeDistro;
    else
    {
      Log.write(1, "fake_distro value is not set. Setting Linux distribution to: " + Defaults.DEFAULT_DISTRIBUTION);
      System.out.println("Warning: fake_distro value is not set. Setting Linux distribution to: " + Defaults.DEFAULT_DISTRIBUTION);
      this.fakeDistro = Defaults.DEFAULT_DISTRIBUTION;
    }

    if(!this.fakeDistro.equals("centos5") && !this.fakeDistro.equals("centos6") && !this.fakeDistro.equals("debian7") && !this.fakeDistro.equals("rhel5") && !this.fakeDistro.equals("rhel6") && !this.fakeDistro.equals("sles11"))
    {
      Log.write(1, "fake_distro value is invalid. Setting Linux distribution to: " + Defaults.DEFAULT_DISTRIBUTION);
      System.out.println("Warning: fake_distro value is invalid. Setting Linux distribution to: " + Defaults.DEFAULT_DISTRIBUTION);
      this.fakeDistro = Defaults.DEFAULT_DISTRIBUTION;
    }
  }

  public byte getFakeProcessors()
  {
    return fakeProcessors;
  }

  public void setFakeProcessors(String fakeProcessors)
  {
    try
    {
      if(fakeProcessors.length() > 0)
        this.fakeProcessors = Byte.parseByte(fakeProcessors);
      else
      {
        Log.write(1, "fake_processors value is not set. Setting number of CPUs to: " + Defaults.DEFAULT_PROCESSORS);
        System.out.println("Warning: fake_processors value is not set. Setting number of CPUs to: " + Defaults.DEFAULT_PROCESSORS);
        this.fakeProcessors = Defaults.DEFAULT_PROCESSORS;
      }
    }
    catch(NumberFormatException nfe)
    {
      Log.write(1, "fake_processors value is invalid. Setting number of CPUs to: " + Defaults.DEFAULT_PROCESSORS);
      System.out.println("Warning: fake_processors value is invalid. Setting number of CPUs to: " + Defaults.DEFAULT_PROCESSORS);
      this.fakeProcessors = Defaults.DEFAULT_PROCESSORS;
    }

    if(this.fakeProcessors < Limits.MIN_PROCESSORS || this.fakeProcessors > Limits.MAX_PROCESSORS)
    {
      Log.write(1, "fake_processors value is out of range. Setting number of CPUs to: " + Defaults.DEFAULT_PROCESSORS);
      System.out.println("Warning: fake_processors value is out of range. Setting number of CPUs to: " + Defaults.DEFAULT_PROCESSORS);
      this.fakeProcessors = Defaults.DEFAULT_PROCESSORS;
    }
  }

  public int getFakeMemory()
  {
    return fakeMemory;
  }

  public void setFakeMemory(String fakeMemory)
  {
    try
    {
      if(fakeMemory.length() > 0)
        this.fakeMemory = Integer.parseInt(fakeMemory);
      else
      {
        Log.write(1, "fake_memory value is not set. Setting amount of RAM to: " + Defaults.DEFAULT_MEMORY);
        System.out.println("Warning: fake_memory value is not set. Setting amount of RAM to: " + Defaults.DEFAULT_MEMORY);
        this.fakeMemory = Defaults.DEFAULT_MEMORY;
      }
    }
    catch(NumberFormatException nfe)
    {
      Log.write(1, "fake_memory value is invalid. Setting amount of RAM to: " + Defaults.DEFAULT_MEMORY);
      System.out.println("Warning: fake_memory value is invalid. Setting amount of RAM to: " + Defaults.DEFAULT_MEMORY);
      this.fakeMemory = Defaults.DEFAULT_MEMORY;
    }

    if(this.fakeMemory < Limits.MIN_MEMORY || this.fakeMemory > Limits.MAX_MEMORY)
    {
      Log.write(1, "fake_memory value is out of range. Setting amount of RAM to: " + Defaults.DEFAULT_MEMORY);
      System.out.println("Warning: fake_memory value is out of range. Setting amount of RAM to: " + Defaults.DEFAULT_MEMORY);
      this.fakeMemory = Defaults.DEFAULT_MEMORY;
    }
  }

  public boolean getHideConsole()
  {
    return hideConsole;
  }

  public void setHideConsole(String hideConsole)
  {
    try
    {
      if(hideConsole.length() > 0)
      {
        if(hideConsole.equals("1") || hideConsole.equalsIgnoreCase("on") || hideConsole.equalsIgnoreCase("enabled"))
          this.hideConsole = true;
        else
          this.hideConsole = Boolean.parseBoolean(hideConsole);
      }
      else
      {
        Log.write(1, "hide_console value is not set. Setting hidden console mode to: Disabled");
        System.out.println("Warning: hide_console value is not set. Setting hidden console mode to: Disabled");
        this.hideConsole = Defaults.DEFAULT_HIDE_CONSOLE;
      }
    }
    catch(NumberFormatException nfe)
    {
      Log.write(1, "hide_console value is invalid. Setting hidden console mode to: Disabled");
      System.out.println("Warning: hide_console value is invalid. Setting hidden console mode to: Disabled");
      this.hideConsole = Defaults.DEFAULT_HIDE_CONSOLE;
    }
  }

  public boolean getDebugMode()
  {
    return debugMode;
  }

  public void setDebugMode(String debugMode)
  {
    try
    {
      if(debugMode.length() > 0)
      {
        if(debugMode.equals("1") || debugMode.equalsIgnoreCase("on") || debugMode.equalsIgnoreCase("enabled"))
          this.debugMode = true;
        else
          this.debugMode = Boolean.parseBoolean(debugMode);
      }
      else
      {
        Log.write(1, "debug_mode value is not set. Setting debug mode to: Enabled");
        System.out.println("Warning: debug_mode value is not set. Setting debug mode to: Enabled");
        this.debugMode = Defaults.DEFAULT_DEBUG_MODE;
      }
    }
    catch(NumberFormatException nfe)
    {
      Log.write(1, "debug_mode value is invalid. Setting debug mode to: Enabled");
      System.out.println("Warning: debug_mode value is invalid. Setting debug mode to: Enabled");
      this.debugMode = Defaults.DEFAULT_DEBUG_MODE;
    }      
  }

  public void parseConfig()
  {
    InputStream inFile = null;
    Properties config = new Properties();

    try
    {
      inFile = new FileInputStream("cfg${file.separator}linulator.properties");
      config.load(inFile);

      setListenAddress(validateOption(config, "listen_address"));
      setEchoPort(validateOption(config, "echo_port"));
      setDaytimePort(validateOption(config, "daytime_port"));
      setChargenPort(validateOption(config, "chargen_port"));
      setTimePort(validateOption(config, "time_port"));
      setFtpPort(validateOption(config, "ftp_port"));
      setSshPort(validateOption(config, "ssh_port"));
      setTelnetPort(validateOption(config, "telnet_port"));
      setSmtpPort(validateOption(config, "smtp_port"));
      setDnsPort(validateOption(config, "dns_port"));
      setHttpPort(validateOption(config, "http_port"));
      setHostName(validateOption(config, "host_name"));
      setRootPassword(validateOption(config, "root_password"));
      setFakeDistro(validateOption(config, "fake_distro"));
      setFakeProcessors(validateOption(config, "fake_processors"));
      setFakeMemory(validateOption(config, "fake_memory"));
      setHideConsole(validateOption(config, "hide_console"));
      setDebugMode(validateOption(config, "debug_mode"));
    }
    catch(IOException ioe)
    {
      System.err.println("Critical: Unable to parse linulator.properties:");
      System.err.println(ioe.getMessage());
      System.exit(1);
    }
    finally
    {
      if(inFile != null)
      {
        try
        {
          inFile.close();
        }
        catch(IOException ioe)
        {
          ioe.printStackTrace();
        }
      }
    }

    validateUniquePorts(echoPort, "echo_port");
    validateUniquePorts(daytimePort, "daytime_port");
    validateUniquePorts(chargenPort, "chargen_port");
    validateUniquePorts(timePort, "time_port");
    validateUniquePorts(ftpPort, "ftp_port");
    validateUniquePorts(sshPort, "ssh_port");
    validateUniquePorts(telnetPort, "telnet_port");
    validateUniquePorts(smtpPort, "smtp_port");
    validateUniquePorts(dnsPort, "dns_port");
    validateUniquePorts(httpPort, "http_port");
  }

  public void showConfig()
  {
    System.out.println("Listen Address: " + getListenAddress());
    System.out.println("Echo Port: " + getEchoPort());
    System.out.println("Daytime Port: " + getDaytimePort());
    System.out.println("Chargen Port: " + getChargenPort());
    System.out.println("Time Port: " + getTimePort());
    System.out.println("FTP Port: " + getFtpPort());
    System.out.println("SSH Port: " + getSshPort());
    System.out.println("Telnet Port: " + getTelnetPort());
    System.out.println("SMTP Port: " + getSmtpPort());
    System.out.println("DNS Port: " + getDnsPort());
    System.out.println("HTTP Port: " + getHttpPort());
    System.out.println("Host Name: " + getShortName());
    System.out.println("Domain Name: " + getDomainName());
    System.out.println("Root Password: " + getMaskedRootPassword() + " (masked)");
    System.out.println("Fake Distro: " + getFakeDistro());
    System.out.println("Fake Processors: " + getFakeProcessors());
    System.out.println("Fake Memory: " + getFakeMemory());
    System.out.println("Hide Console: " + getHideConsole());
    System.out.println("Debug Mode: " + getDebugMode());
  }

  public String validateOption(Properties config, String option)
  {
    if(config.getProperty(option) == null || config.getProperty(option).length() == 0)
      return "";
    else
      return config.getProperty(option);
  }

  public int validatePort(String port1, int port2, String serviceName, String option)
  {
    try
    {
      if(port1.length() > 0)
        port2 = Integer.parseInt(port1);
      else
        port2 = 0;
    }
    catch(NumberFormatException nfe)
    {
      Log.write(1, "The " + serviceName + " service has been disabled since " + option + " value is invalid: " + port1);
      System.out.println("Warning: The " + serviceName + " service has been disabled since " + option + " value is invalid: " + port1);
      port2 = 0; // disable service
      return port2;
    }

    if(port2 < Limits.MIN_PORT || port2 > Limits.MAX_PORT)
    {
      Log.write(1, "The " + serviceName + " service has been disabled since " + option + " value is out of range: " + port2);
      System.out.println("Warning: The " + serviceName + " service has been disabled since " + option + " value is out of range: " + port2);
      port2 = 0;
    }

    if(port2 == 0)
    {
      Log.write(0, "The " + serviceName + " service has been disabled since " + option + " value is not set.");
      System.out.println("The " + serviceName + " service has been disabled since " + option + " value is not set.");
    }

    return port2;
  }

  public void validateUniquePorts(int port, String option)
  {
    byte matchCount = 0;

    if(port > 0)
    {
      if(port == echoPort)
        matchCount++;
      if(port == daytimePort)
        matchCount++;
      if(port == chargenPort)
        matchCount++;
      if(port == timePort)
        matchCount++;
      if(port == ftpPort)
        matchCount++;
      if(port == sshPort)
        matchCount++;
      if(port == telnetPort)
        matchCount++;
      if(port == smtpPort)
        matchCount++;
      if(port == dnsPort)
        matchCount++;
      if(port == httpPort)
        matchCount++;
    }

    if(matchCount > 1)
    {
      Log.write(2, "Modify the " + option + " value in linulator.properties. The port number is not unique: " + port);
      System.err.println("Critical: Modify the " + option + " value in linulator.properties. The port number is not unique: " + port);
      System.exit(1);
    }
  }
}
