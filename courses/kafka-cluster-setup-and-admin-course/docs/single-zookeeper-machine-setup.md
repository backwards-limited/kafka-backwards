# Single Zookeeper Machine Setup

Goals:

- SSH into our machine
- Install some necessary (Java) and helpful packages on the machine
- Disable RAM Swap
- Add hosts mapping from hostname to public ips to /etc/hosts
- Download and configure Zookeeper on the machine
- Launch Zookeeper on the machine to test
- Setup Zookeeper as a service on the machine

SSH onto your VM instance and following along.

## Install packages

```bash
ubuntu@ip-172-31-9-1:~$ sudo apt-get update && \
    sudo apt-get -y install wget ca-certificates zip net-tools vim nano tar netcat
```

## Java Open JDK 8

```bash
ubuntu@ip-172-31-9-1:~$ sudo apt-get -y install openjdk-8-jdk
```

```bash
ubuntu@ip-172-31-9-1:~$ java -version
```

## Disable RAM Swap

Can be set to 0 on certain Linux distributions but we'll have 1. Without this setting you may encounter performance issues with your setup. Let's just see the initial setting:

```bash
ubuntu@ip-172-31-9-1:~$ sudo sysctl vm.swappiness
vm.swappiness = 60
```

```bash
ubuntu@ip-172-31-9-1:~$ sudo sysctl vm.swappiness=1
vm.swappiness = 1
```

But to keep this setting upon a reboot, we need to append to /etc/sysctl.conf:

```bash
ubuntu@ip-172-31-9-1:~$ echo 'vm.swappiness=1' | sudo tee --append /etc/sysctl.conf
vm.swappiness=1
```

## Add hosts entries (mocking DNS)

Use relevant IPs here.

```bash
ubuntu@ip-172-31-9-1:~$ echo "172.31.9.1 kafka1
172.31.9.1 zookeeper1
172.31.19.230 kafka2
172.31.19.230 zookeeper2
172.31.35.20 kafka3
172.31.35.20 zookeeper3" | sudo tee --append /etc/hosts
```

```bash
ubuntu@ip-172-31-9-1:~$ ping kafka1
PING kafka1 (172.31.9.1) 56(84) bytes of data.
64 bytes from kafka1 (172.31.9.1): icmp_seq=1 ttl=64 time=0.017 ms
64 bytes from kafka1 (172.31.9.1): icmp_seq=2 ttl=64 time=0.031 ms
```

## Download Zookeeper and Kafka

```bash
ubuntu@ip-172-31-9-1:~$ wget http://www-us.apache.org/dist/kafka/2.2.1/kafka_2.12-2.2.1.tgz

ubuntu@ip-172-31-9-1:~$ tar -xvzf kafka_2.12-2.2.1.tgz

ubuntu@ip-172-31-9-1:~$ rm kafka_2.12-2.2.1.tgz

ubuntu@ip-172-31-9-1:~$ mv kafka_2.12-2.2.1 kafka
```

## Zookeeper quickstart

```bash
ubuntu@ip-172-31-9-1:~$ cd kafka

ubuntu@ip-172-31-9-1:~/kafka$ cat config/zookeeper.properties
# the directory where the snapshot is stored.
dataDir=/tmp/zookeeper
# the port at which the clients will connect
clientPort=2181
# disable the per-ip limit on the number of connections since this is a non-production config
maxClientCnxns=0
```

The following will bind to port 2181 - we are good to go. Ctrl-C to exit.

```bash
ubuntu@ip-172-31-9-1:~/kafka$ bin/zookeeper-server-start.sh config/zookeeper.properties
...
INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
```

and for now Ctrl-C

Now start Zookeeper in the background so we can quickly test it:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
```

Run the Zookeeper shell to test:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ bin/zookeeper-shell.sh localhost:2181
Connecting to localhost:2181
Welcome to ZooKeeper!
JLine support is disabled

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
```

and type an ls command:

```bash
ls /
[zookeeper]
```

Ctrl-C out of this and ask Zookeeper if it is ok:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ echo "ruok" | nc localhost 2181 ; echo
imok
```

## Zookeeper as service - Install Zookeeper boot scripts

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo nano /etc/init.d/zookeeper
```

and copy the following into the editor:

```bash
#!/bin/sh
#
# zookeeper          Start/Stop zookeeper
#
# chkconfig: - 99 10
# description: Standard script to start and stop zookeeper

DAEMON_PATH=/home/ubuntu/kafka/bin
DAEMON_NAME=zookeeper

PATH=$PATH:$DAEMON_PATH

# See how we were called.
case "$1" in
  start)
        # Start daemon.
        pid=`ps ax | grep -i 'org.apache.zookeeper' | grep -v grep | awk '{print $1}'`
        if [ -n "$pid" ]
          then
            echo "Zookeeper is already running";
        else
          echo "Starting $DAEMON_NAME";
          $DAEMON_PATH/zookeeper-server-start.sh -daemon /home/ubuntu/kafka/config/zookeeper.properties
        fi
        ;;
  stop)
        echo "Shutting down $DAEMON_NAME";
        $DAEMON_PATH/zookeeper-server-stop.sh
        ;;
  restart)
        $0 stop
        sleep 2
        $0 start
        ;;
  status)
        pid=`ps ax | grep -i 'org.apache.zookeeper' | grep -v grep | awk '{print $1}'`
        if [ -n "$pid" ]
          then
          echo "Zookeeper is Running as PID: $pid"
        else
          echo "Zookeeper is not Running"
        fi
        ;;
  *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
esac

exit 0
```

Ctrl-O and Ctrl-X.

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo chmod +x /etc/init.d/zookeeper

ubuntu@ip-172-31-9-1:~/kafka$ sudo chown root:root /etc/init.d/zookeeper

ubuntu@ip-172-31-9-1:~/kafka$ sudo update-rc.d zookeeper defaults
```

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo service zookeeper stop
```

Verify we've stopped Zookeeper:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ nc -vz localhost 2181
```

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo service zookeeper start

ubuntu@ip-172-31-9-1:~/kafka$ nc -vz localhost 2181
Connection to localhost 2181 port [tcp/*] succeeded!
```

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo service zookeeper status
● zookeeper.service - SYSV: Standard script to start and stop zookeeper
   Loaded: loaded (/etc/init.d/zookeeper; generated)
   Active: active (exited) since Mon 2019-09-23 21:31:22 UTC; 1min 8s ago
     Docs: man:systemd-sysv-generator(8)
  Process: 20124 ExecStart=/etc/init.d/zookeeper start (code=exited, status=0/SUCCESS)

Sep 23 21:31:22 ip-172-31-9-1 systemd[1]: Starting SYSV: Standard script to start and stop zookeeper...
Sep 23 21:31:22 ip-172-31-9-1 zookeeper[20124]: Zookeeper is already running
Sep 23 21:31:22 ip-172-31-9-1 systemd[1]: Started SYSV: Standard script to start and stop zookeeper.
```

```bash
ubuntu@ip-172-31-9-1:~/kafka$ cat logs/zookeeper.out
[2019-09-23 20:35:17,870] INFO Reading configuration from: config/zookeeper.properties 
...
[2019-09-23 20:35:17,958] INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
[2019-09-23 20:37:01,245] INFO Accepted socket connection from /127.0.0.1:33186 (org.apache.zookeeper.server.NIOServerCnxnFactory)
[2019-09-23 20:37:01,260] INFO Client attempting to establish new session at /127.0.0.1:33186 (org.apache.zookeeper.server.ZooKeeperServer)
[2019-09-23 20:37:01,263] INFO Creating new log file: log.1
...
[2019-09-23 20:40:07,867] INFO The list of known four letter word commands is : [{1936881266=srvr, 1937006964=stat, 2003003491=wchc, 1685417328=dump, 1668445044=crst, 1936880500=srst, 1701738089=envi, 1668247142=conf, 2003003507=wchs, 2003003504=wchp, 1668247155=cons, 1835955314=mntr, 1769173615=isro, 1920298859=ruok, 1735683435=gtmk, 
...
```

## Using Zookeeper CLI

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo service zookeeper start

ubuntu@ip-172-31-9-1:~/kafka$ nc -vz localhost 2181
```

```bash
ubuntu@ip-172-31-9-1:~/kafka$ bin/zookeeper-shell.sh localhost:2181
```

Inside the shell we can type **help**:

```bash
help
ZooKeeper -server host:port cmd args
	stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history
	redo cmdno
	printwatches on|off
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit
	getAcl path
	close
	connect host:port
```

Continue inside the shell:

```bash
create /my-node "foo"
Created /my-node

ls /
[zookeeper, my-node]

get /my-node
foo
cZxid = 0x4
ctime = Tue Sep 24 21:35:37 UTC 2019
mZxid = 0x4
mtime = Tue Sep 24 21:35:37 UTC 2019
pZxid = 0x4
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0

create /my-node/deeper-node "bar"
Created /my-node/deeper-node

ls /
[zookeeper, my-node]
ls /my-node
[deeper-node]

get /my-node/deeper-node
bar
cZxid = 0x5
...

rmr /my-node

# Create a watcher
create /node-to-watch ""
get /node-to-watch true
set /node-to-watch "has-changed"
WATCHER::

WatchedEvent state:SyncConnected type:NodeDataChanged path:/node-to-watch
cZxid = 0x8
...

rmr /node-to-watch
```

