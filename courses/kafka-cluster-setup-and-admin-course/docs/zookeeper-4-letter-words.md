# Zookeeper 4 Letter Words

**Configuration**:

```bash
ubuntu@ip-172-31-9-1:~/kafka$ echo "conf" | nc localhost 2181
clientPort=2181
dataDir=/data/zookeeper/version-2
dataLogDir=/data/zookeeper/version-2
tickTime=2000
maxClientCnxns=0
minSessionTimeout=4000
maxSessionTimeout=40000
serverId=1
initLimit=10
syncLimit=5
electionAlg=3
electionPort=3888
quorumPort=2888
peerType=0
```

**Connections** - connection/session details for all clients connected to this server. Includes information on numbers of packets received/sent, session id, operation latencies, last operation performed, etc:

```bash
ubuntu@ip-172-31-9-1:~$ echo "cons" | nc localhost 2181
 /127.0.0.1:59382[0](queued=0,recved=1,sent=0)
```

**Reset connections** - reset connection/session statistics for all connections:

```bash
ubuntu@ip-172-31-9-1:~$ echo "crst" | nc localhost 2181
Connection stats reset.
```

**Dump** - lists the outstanding sessions and ephemeral nodes. This only works on the leader:

```bash
ubuntu@ip-172-31-9-1:~$ echo "dump" | nc localhost 2181
SessionTracker dump:
org.apache.zookeeper.server.quorum.LearnerSessionTracker@15b8bc61
ephemeral nodes dump:
Sessions with Ephemerals (0):
```

**Environment** - details about serving environment

```bash
ubuntu@ip-172-31-9-1:~$ echo "envi" | nc localhost 2181
Environment:
zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 00:39 GMT
host.name=kafka1
java.version=1.8.0_222
java.vendor=Private Build
java.home=/usr/lib/jvm/java-8-openjdk-amd64/jre
java.class.path=/home/ubuntu/kafka/bin/../libs/activation-1.1.1.jar:...
java.library.path=/usr/java/packages/lib/amd64:/usr/lib/x86_64-linux-gnu/jni:/lib/x86_64-linux-gnu:/usr/lib/x86_64-linux-gnu:/usr/lib/jni:/lib:/usr/lib
java.io.tmpdir=/tmp
java.compiler=<NA>
os.name=Linux
os.arch=amd64
os.version=4.15.0-1050-aws
user.name=root
user.home=/root
user.dir=/
```

**ruok** - tests if server is running in a non-error state:

```bash
ubuntu@ip-172-31-9-1:~$ echo "ruok" | nc localhost 2181
imok
```

**Reset server stats**:

```bash
ubuntu@ip-172-31-9-1:~$ echo "srst" | nc localhost 2181
Server stats reset.
```

**Server details**:

```bash
ubuntu@ip-172-31-9-1:~$ echo "srvr" | nc localhost 2181
Zookeeper version: 3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 00:39 GMT
Latency min/avg/max: 0/0/0
Received: 1
Sent: 1
Connections: 1
Outstanding: 0
Zxid: 0x300000007
Mode: follower
Node count: 5
```

**Stats** - brief details for the server and connected clients:

```bash
ubuntu@ip-172-31-9-1:~$ echo "stat" | nc localhost 2181
Zookeeper version: 3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 00:39 GMT
Clients:
 /127.0.0.1:59398[0](queued=0,recved=1,sent=0)

Latency min/avg/max: 0/0/0
Received: 2
Sent: 2
Connections: 1
Outstanding: 0
Zxid: 0x300000007
Mode: follower
Node count: 5
```

**Watches** - information on watches for the server:

```bash
ubuntu@ip-172-31-9-1:~$ echo "wchs" | nc localhost 2181
0 connections watching 0 paths
Total watches:0
```

**Watches for the server, by session**:

```bash
ubuntu@ip-172-31-9-1:~$ echo "wchc" | nc localhost 2181
wchc is not executed because it is not in the whitelist.
```

**Watches for the server, by path**:

```bash
ubuntu@ip-172-31-9-1:~$ echo "wchp" | nc localhost 2181
wchp is not executed because it is not in the whitelist.
```

**Monitoring the health of the cluster**:

```bash
ubuntu@ip-172-31-9-1:~$ echo "mntr" | nc localhost 2181
zk_version	3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 00:39 GMT
zk_avg_latency	0
zk_max_latency	0
zk_min_latency	0
zk_packets_received	6
zk_packets_sent	6
zk_num_alive_connections	1
zk_outstanding_requests	0
zk_server_state	follower
zk_znode_count	5
zk_watch_count	0
zk_ephemerals_count	0
zk_approximate_data_size	44
zk_open_file_descriptor_count	115
zk_max_file_descriptor_count	4096
zk_fsync_threshold_exceed_count	0
```

