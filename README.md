LinPot
======

A Linux honeypot written in C

The goal is to create a fake Linux environment with configurable network ports that can be opened.
This will hopefully attract potential attackers so that their methods can be learned. The modified
environment can be frozen and saved to disk for forensic analysis. Extensive logging can also be
employed.

Note: This project is a work in progress and only does the following so far:

* Launches as a daemon and changes effective user and group ID
* Can log messages based on severity
* Parses a configuration file

The next feature I am working on is adding network support so you can telnet to the honeypot for
example.

Since the network services LinPot uses typically run on privileged ports and it is not recommended
that you run LinPot as root, you should run the services on ports >1024 and configure your firewall
to redirect the traffic to the higher-numbered ports. You may also choose to run the program within
a zone, jail, or chrooted for added security on the host system.

Short FAQ
=========

1.) Why create a fake Linux environment when you can have a real one?

I am creating this project mainly for learning purposes for myself. I also wanted to create something
even more disposable than a virtual machine with less setup time.

2.) How can an attacker gain access to the LinPot environment?

The idea is to allow the attacker access via ftp and telnet (and later ssh) after they discover a
user/password combination with the password being intentionally weak. They can then navigate around
the artificial system while LinPot logs their moves on the host system. They may attempt to remove
log files, modify files, plant rootkits, etc.
