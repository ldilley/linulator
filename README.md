LinPot
======

A Linux honeypot

The goal is to create a fake Linux environment with configurable network ports that can be opened.
This will hopefully attract potential attackers so that their methods can be learned. The modified
environment can be frozen and saved to disk for forensic analysis. Extensive logging can also be
employed.

Note: This project is still very much a work in progress! There is no virtual filesystem in place yet,
SSH is not implemented, and many commands still need to be added.

Since the network services LinPot uses typically run on privileged ports and it is not recommended
that you run LinPot as root, you should run the services on ports >1024 and configure your firewall
to redirect the traffic to the higher-numbered ports. You may also want to run the program within
a virtual machine, container, zone, jail, or chroot environment for added security on the host
system.

Installation
============

**Prerequisites**: Apache Ant and the JDK

1.) Build the jar:  
*ant*

2.) Change to the dist directory:  
*cd dist*

3.) Modify the configuration file to your liking:  
*vi linpot.properties*

4.) Optionally modify the security policy to meet your needs (the default should suffice):  
*vi linpot.policy*

5.) Forward or redirect any ports you want to use with LinPot. If you have a firewall/NAT appliance
or broadband router, you can simply have LinPot listen on higher-numbered ports and forward the
standard port numbers. For example: outside:23 -> inside:10023 for telnet. If the host is reachable
from the Internet and LinPot is listening on nonstandard unprivileged ports, you will want to
redirect traffic from the standard ports. This can be done using iptables, ipfw, or similar. Replace
the addresses, interfaces, and ports in the examples below with your own.

**FreeBSD**  
Modify /etc/rc.conf and add the following line:  
*pf_enable="YES"*

Add similar lines for each service to /etc/pf.conf:  
*rdr pass on em0 proto tcp from any to 192.168.1.7 port 10023 -> 192.168.1.7 port 23*

Start the packet filter:  
*/etc/rc.d/pf start*

Confirm the rules are in memory:  
*pfctl -sn*

**Linux**  
Run similar commands for each service (see the documentation for your distribution to make the rule persistent):  
*iptables -t nat -I PREROUTING --src 0/0 --dst 192.168.1.7 -p tcp --dport 23 -j REDIRECT --to-ports 10023*

Confirm the rules are in memory:  
*iptables -L*

**OS X <= 10.6 (Snow Leopard)**  
Run similar commands for each service (and replace the rule numbers with your own):  
*sudo ipfw add 101 fwd 192.168.1.7,10023 tcp from any to me 23*

Confirm the rules are in memory:  
*sudo ipfw list*

To make the rules persistent, you can add them to /etc/ipfw.conf and create a launch agent or daemon
to load them automatically (use a search engine for details.)

**OS X 10.7 (Lion), 10.8 (Mountain Lion), and 10.9 (Mavericks)**  
Note: ipfw was deprecated in 10.7 (Lion). Use pf instead.

Add similar lines for each service to /etc/pf.conf:  
*rdr on en0 inet proto tcp to 192.168.1.7 port 23 -> 192.168.1.7 port 10023*

Load the changes:  
*sudo pfctl -f /etc/pf.conf*

Enable the packet filter:  
*sudo pfctl -e*

Confirm the rules are in memory:  
*sudo pfctl -sn*

**Solaris**  
Add similar lines for each service to /etc/ipf/ipnat.conf:  
*rdr e1000g0 from any to 192.168.1.7 port = 23 -> 192.168.1.7 port 10023 tcp*

Enable the IP filter:  
*svcadm enable ipfilter*

Confirm the rules are in memory:  
*ipnat -l*

6.) Launch LinPot (where X.X.X is the version and YYYYMMDD is the build date):  
*java -jar linpot-X.X.X-YYYYMMDD.jar*

7.) Enjoy!

Short FAQ
=========

1.) Why create a fake Linux environment when you can have a real one?

I am creating this project mainly for learning purposes. I also wanted to create something even
more disposable than a virtual machine with less setup time.

2.) How can an attacker gain access to the LinPot environment?

The idea is to allow the attacker access via ftp and telnet (and later ssh) after they discover a
user/password combination with the password being intentionally weak. They can then navigate around
the artificial system while LinPot logs their moves to the host system. They may attempt to remove
log files, modify files, plant rootkits, etc. In the meanwhile, you will be sitting safely outside
of this figurative ant farm watching (and possibly laughing maniacally?) the insect work. >;)

3.) Why Java?

I originally intended to use C for this project, but Java has some benefits I wanted to take
advantage of. Java has a large API. As a result, there is unlikely a need to go in search of
third-party libraries or re-invent the wheel. Java is also more portable and eliminates the
requirement for things like #ifdef and cmake. Another very important and particularly applicable
aspect for LinPot is security. Java performs bounds checking on arrays which can significantly
reduce the risk of buffer overflows (unless there is a vulnerability in the JVM of course.) The
SecurityManager class is additionally essential since it limits what users can do within LinPot.
Lastly, a consequence of automatic memory management is that there are no explicit pointers. This
increases safety by preventing dangling/wild pointers and defends against memory leaks.
