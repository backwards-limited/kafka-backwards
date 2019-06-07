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

## Simple Multi Agent Flow

```markdown
Agent (start this one second)                            Agent (start first)

Source       —>          Channel  —>  Sink      —>       Source    —>    Channel   —>  Sink

Log data from web logs   Memory       Avro               Avro	           Memory        logger
```

Take a look at [first-agent.conf](../src/main/resources/simple-multi/first-agent.conf) and [second-agent.conf](../src/main/resources/simple-multi/second-agent.conf).

Start the first agent:

```bash
$ flume-ng agent --name fa --conf-file first-agent.conf
...
INFO node.Application: Starting Sink k1
INFO node.Application: Starting Source r1
INFO source.AvroSource: Starting Avro source r1: { bindAddress: 0.0.0.0, port: 44444 }...
INFO instrumentation.MonitoredCounterGroup: Monitored counter group for type: SOURCE, name: r1: Successfully registered new MBean.
INFO instrumentation.MonitoredCounterGroup: Component type: SOURCE, name: r1 started
INFO source.AvroSource: Avro source r1 started.
```

Start the second agent:

```bash
$ flume-ng agent --name sa --conf-file second-agent.conf
```

## Piping Data into HDFS via Flume

We have configurations under [sink-multi](../src/main/resources/flume/sink-multi) where we can run a [first attempt](../src/main/resources/flume/sink-multi/logs-to-logger.conf) not actually using HDFS:

```bash
$ flume-ng agent --name lm --conf-file logs-to-logger.conf
```

Now for our [hdfs conf](../src/main/resources/flume/sink-multi/logs-to-hdfs.conf)

```bash
$ hadoop fs -ls ./hdfs
2019-06-03 21:45:42,345 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
```
With our logs generator running i.e. start GenLogs e.g.

```bash
$ sbt "streaming-kafka-course/runMain com.backwards.kafka.streaming.demo.GenLogs"
```

then:

```bash
$ flume-ng agent --name lm --conf-file logs-to-hdfs.conf
```

We'll start to get a lot of files under **hdfs**:

![HDFS files](images/hdfs-files.png)

## Piping Data into Kafka via Flume

Just as with the previous example but this time we have the following flume manifest named [logs-to-hdfs-and-kafka.conf](../src/main/resources/flume/sink-multi-with-kafka/logs-to-hdfs-and-kafka.conf):

```properties
lm.sources = r1
lm.sinks = k1 k2
lm.channels = c1 c2

lm.sources.r1.type = exec
lm.sources.r1.command = tail -F ../../logs.txt

lm.sinks.k1.type = hdfs
lm.sinks.k1.hdfs.path = ./hdfs
lm.sinks.k1.hdfs.fileType = DataStream
lm.sinks.k1.hdfs.fileSuffix = .txt
lm.sinks.k1.hdfs.rollInterval = 60
lm.sinks.k1.hdfs.rollSize = 0
lm.sinks.k1.hdfs.rollCount = 100

lm.sinks.k2.type = org.apache.flume.sink.kafka.KafkaSink
lm.sinks.k2.kafka.bootstrap.servers = localhost:9092
lm.sinks.k2.kafka.topic = flume-logs-to-kafka

lm.channels.c1.type = memory
lm.channels.c1.capacity = 1000
lm.channels.c1.transactionCapacity = 100

lm.channels.c2.type = memory
lm.channels.c2.capacity = 1000
lm.channels.c2.transactionCapacity = 100

lm.sources.r1.channels = c1 c2
lm.sinks.k1.channel = c1
lm.sinks.k2.channel = c2
```

Of course this time we will also need Kafka up and running - You can use one of our available [docker compose files](../src/it/resources/docker-compose.yml).

And again start the flume agent - don't forget to have the GenLogs Scala application running:

```bash
$ flume-ng agent --name lm --conf-file logs-to-hdfs-and-kafka.conf
```

We can check the flume piped messages into kafka:

```bash
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic flume-logs-to-kafka

16.255.203.116 - [07-06-2019 22:16:46] "GET /admin HTTP/1.1" 200
70.98.81.136 - [07-06-2019 22:16:51] "GET /departments HTTP/1.1" 200
...
```

