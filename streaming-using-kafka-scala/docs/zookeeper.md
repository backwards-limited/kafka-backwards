# Zookeeper

Make sure zookeeper is up and running via [docker-compose.yml](../src/main/resources/docker-compose.yml).

## Commands

```bash
$ zookeeper-shell localhost:2181 help
```

```bash
$ zookeeper-shell localhost:2181 get /zookeeper
Connecting to localhost:2181

WATCHER::

WatchedEvent state:SyncConnected type:None path:null

cZxid = 0x0
ctime = Thu Jan 01 01:00:00 GMT 1970
mZxid = 0x0
mtime = Thu Jan 01 01:00:00 GMT 1970
pZxid = 0x0
cversion = -1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 1
```

```bash
$ echo conf | nc localhost 2181
clientPort=2181
dataDir=/data/version-2
dataLogDir=/datalog/version-2
tickTime=2000
maxClientCnxns=60
minSessionTimeout=4000
maxSessionTimeout=40000
serverId=1
initLimit=5
syncLimit=2
electionAlg=3
electionPort=3888
quorumPort=2888
peerType=0
```

```bash
$ echo stat | nc localhost 2181
Zookeeper version: 3.4.9-1757313, built on 08/23/2016 06:50 GMT
Clients:
 /172.18.0.8:58334[1](queued=0,recved=3331,sent=3335)
 /172.18.0.7:54958[1](queued=0,recved=2988,sent=2988)
 /172.18.0.9:60656[1](queued=0,recved=458,sent=458)
 /172.18.0.1:55890[0](queued=0,recved=1,sent=0)

Latency min/avg/max: 0/0/43
Received: 6787
Sent: 6790
Connections: 4
Outstanding: 0
Zxid: 0x2000000d5
Mode: follower
Node count: 144
```

