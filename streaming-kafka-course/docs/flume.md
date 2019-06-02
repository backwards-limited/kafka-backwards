# Apache Flume

```bash
$ brew install flume
```

![Flume](images/flume.png)

Flume is a distributed, reliable, and available service for efficiently collecting, aggregating, and moving large amounts of log data.

Flume components:

![Flume components](images/flume-components.png)

We have a flume agent (which is source, sink and channel) [example](../src/main/resources/flume/flume-example.conf) which can be bootstrapped as:

```bash
$ flume-ng agent --name a1 --conf-file flume-example.conf
...
2019-05-30 22:50:45,479 INFO node.Application: Starting Sink k1
2019-05-30 22:50:45,480 INFO node.Application: Starting Source r1
2019-05-30 22:50:45,481 INFO source.NetcatSource: Source starting
2019-05-30 22:50:45,527 INFO source.NetcatSource: Created serverSocket:sun.nio.ch.ServerSocketChannelImpl[/0:0:0:0:0:0:0:0:44444]
```

Telnet to issue some messages which we will see in the flume logs:

```bash
$ telnet localhost 44444
Trying ::1...
Connected to localhost.
Escape character is '^]'.
Scala
OK
Haskell
OK
Kafka
OK
Cassandra
OK
Blockchain
OK
```

and back to flume we'll see:

```bash
INFO sink.LoggerSink: Event: { headers:{} body: 53 63 61 6C 61 0D Scala. }
INFO sink.LoggerSink: Event: { headers:{} body: 48 61 73 6B 65 6C 6C 0D Haskell. }
INFO sink.LoggerSink: Event: { headers:{} body: 4B 61 66 6B 61 0D Kafka. }
INFO sink.LoggerSink: Event: { headers:{} body: 43 61 73 73 61 6E 64 72 61 0D Cassandra. }
INFO sink.LoggerSink: Event: { headers:{} body: 42 6C 6F 63 6B 63 68 61 69 6E 0D Blockchain.}
```

## Source, Sink and Channel

- Source is primarily to read data from web server logs. There are several types of sources:
  - netcat (as in the above example)
  - exec
  - syslog
  - avro
  - and more...

- Sink is primarily to write data into data stores or other Flume agent sources (via avro). There are several types of sinks:
  - logger
  - HDFS
  - avro
  - and more...

- Channel data between source and sink. There are several types where the most popular are:
  - Memory (good performance but unreliable)
  - File (reliable at the cost of performance)
  - Kafka