# Zookeeper Quorum Setup

Goals:

- Create an AMI (image) from the existing machine
- Create other 2 machines and launch Zookeeper on them
- Test that the Quorum is running and working

![Creating AMI image](images/creating-ami-image.png)

---

![Create AMI image](images/create-ami-image.png)

---

![View AMI images](images/view-ami-images.png)

---

![AMI snapshot image](images/ami-snapshot-image.png)

---

![AMI image](images/ami-image.png)

---

![Instance type from AMI](images/instance-type-from-ami.png)

This time we choose **eu-west-2b** which has IP **172.31.16.0**, and from this we can set a **primary IP** within **network interfaces** as say **172.31.19.230**:

![AMI configure instance](images/ami-configure-instance.png)

---

![AMI tag](images/ami-tag.png)

---

![AMI security group](images/ami-security-group.png)

---

![Two EC2 instances](images/two-ec2-instances.png)

And let's launch our third instance with IP of **172.31.35.20**:

![AMI image](images/ami-image.png)

---

![Third configure instance](images/third-configure-instance.png)

---

![Server 3 tag](images/server-3-tag.png)

---

![3 instances](images/three-instances.png)

## SSH onto the three instances

![Three shells](images/three-shells.png)

---

![SSH logged in](images/ssh-logged-in.png)

Start Zookeeper on all 3 servers e.g. on Server 1:

```bash
ubuntu@ip-172-31-9-1:~$ sudo service zookeeper start
```

Then check on each server that indeed Zookeeper is running e.g. on Server 1:

```bash
ubuntu@ip-172-31-9-1:~$ nc -vz localhost 2181
```

Instead we could also ping e.g. on Server 1:

```bash
ubuntu@ip-172-31-9-1:~$ ping zookeeper1
```

Also try running the following on Server1, to see if we can hit Zookeeper on Server2:

```bash
ubuntu@ip-172-31-9-1:~$ nc -vz zookeeper2 2181
```

Next, stop Zookeeper on all instance e.g.

```bash
ubuntu@ip-172-31-9-1:~$ sudo service zookeeper stop
```

## Create Quorum

Do the following on all 3 instances:

```bash
# create data dictionary for zookeeper with ubuntu as the owner
ubuntu@ip-172-31-9-1:~$ sudo mkdir -p /data/zookeeper

ubuntu@ip-172-31-9-1:~$ sudo chown -R ubuntu:ubuntu /data/
```

Set identities of 1, 2 and 3:

```bash
# Declare the server's identity
ubuntu@ip-172-31-9-1:~$ echo "1" > /data/zookeeper/myid
```

Double check:

```bash
ubuntu@ip-172-31-9-1:~$ cat /data/zookeeper/myid
```

Next, change Zookeeper properties:

```bash
# Edit the zookeeper settings
ubuntu@ip-172-31-9-1:~$ rm /home/ubuntu/kafka/config/zookeeper.properties
ubuntu@ip-172-31-9-1:~$ nano /home/ubuntu/kafka/config/zookeeper.properties
```

In **nano** for each instance add the following:

```bash
# The location to store the in-memory database snapshots and, unless specified otherwise,
# the transaction log of updates to the database.
dataDir=/data/zookeeper

# The port at which the clients will connect
clientPort=2181

# Disable the per-ip limit on the number of connections since this is a non-production config
maxClientCnxns=0

# The basic time unit in milliseconds used by ZooKeeper.
# It is used to do heartbeats and the minimum session timeout will be twice the tickTime.
tickTime=2000

# The number of ticks that the initial synchronization phase can take
initLimit=10

# The number of ticks that can pass between sending a request and getting an acknowledgement
syncLimit=5

# Zookeeper servers - these hostnames such as `zookeeper1` come from the /etc/hosts file
server.1=zookeeper1:2888:3888
server.2=zookeeper2:2888:3888
server.3=zookeeper3:2888:3888

```

Ctrl-X each editor, and double check:

```bash
ubuntu@ip-172-31-9-1:~$ cd kafka
ubuntu@ip-172-31-9-1:~/kafka$ cat config/zookeeper.properties
```

Now if we start each Zookeeper again, this time manually to see the logs, initially there will be errors because of trying to connect to other Zookeepers i.e. there will be errors until all servers are up:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

And then shutdown the servers again.

Let's run them all as a *service* and check *ruok*:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ sudo service zookeeper start
```

```bash
ubuntu@ip-172-31-9-1:~/kafka$ echo "ruok" | nc localhost 2181 ; echo
ubuntu@ip-172-31-9-1:~/kafka$ echo "ruok" | nc zookeeper1 2181 ; echo
ubuntu@ip-172-31-9-1:~/kafka$ echo "ruok" | nc zookeeper2 2181 ; echo
ubuntu@ip-172-31-9-1:~/kafka$ echo "ruok" | nc zookeeper3 2181 ; echo
```

Take a look at the stats on each instance, where one is the leader:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ echo "stat" | nc localhost 2181 ; echo

Zookeeper version: 3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 00:39 GMT
Clients:
 /127.0.0.1:56154[0](queued=0,recved=1,sent=0)

Latency min/avg/max: 0/0/0
Received: 3
Sent: 2
Connections: 1
Outstanding: 0
Zxid: 0x300000000
Mode: leader
Node count: 4
Proposal sizes last/min/max: -1/-1/-1
```

Finally, start a Zookeeper shell on each instance:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ bin/zookeeper-shell.sh zookeeper1:2181
...
create /my-node "some data"
Created /my-node

ls /
[zookeeper, my-node]
```

Running **ls /** from other instances will also see the newly created node **my-node**.

And on some other instance run:

```bash
get /my-node
some data
...
```

