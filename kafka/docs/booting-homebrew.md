# Homebrew Booting Kafka

From [Setup](../../docs/setup.md) you should already have Kafka "homebrewed", where the essential requirement is:

```bash
brew install kafka
```

To boot:

```bash
$ brew services start zookeeper
$ brew services start kafka

$ brew services stop kafka
$ brew services stop zookeeper
```

and where are the associated configurations?

```bash
$ ls -las /usr/local/etc/kafka
...
16 -rw-r--r--   1 davidainslie  admin  5685  8 Oct 16:55 server.properties
...
```

## Can we Start a Cluster?

To start a cluster locally, we have to use specific configurations e.g. different **server.properties** to use different **broker IDs**, **ports**, and **data directories**.

In some directory, copy the original **server.properties** from /usr/local/etc/kafka amending as follows:

```bash
$ cp server.properties server.2.properties
$ cp server.properties server.3.properties
$ cp server.properties server.4.properties
```

amending each new file with the changes for 2, 3 and 4:

```bash
broker.id = 2
listeners = PLAINTEXT://:9092
log.dirs = /usr/local/var/lib/kafka-logs-2
```

and boot (noting that we use the *kafka-server-start* script allowing us to provide a specific configuration):

```bash
$ brew services start zookeeper

$ kafka-server-start /usr/local/etc/kafka/server.2.properties
$ kafka-server-start /usr/local/etc/kafka/server.3.properties
$ kafka-server-start /usr/local/etc/kafka/server.4.properties
```

Create a topic:

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --create --topic many-reps --partitions 6 --replication-factor 3
Created topic "many-reps"
```

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --describe --topic many-reps
Topic:many-reps	PartitionCount:6	ReplicationFactor:3	Configs:
	Topic: many-reps	Partition: 0	Leader: 4	Replicas: 4,2,3	Isr: 4,2,3
	Topic: many-reps	Partition: 1	Leader: 2	Replicas: 2,3,4	Isr: 2,3,4
	Topic: many-reps	Partition: 2	Leader: 3	Replicas: 3,4,2	Isr: 3,4,2
	Topic: many-reps	Partition: 3	Leader: 4	Replicas: 4,3,2	Isr: 4,3,2
	Topic: many-reps	Partition: 4	Leader: 2	Replicas: 2,4,3	Isr: 2,4,3
	Topic: many-reps	Partition: 5	Leader: 3	Replicas: 3,2,4	Isr: 3,2,4
```

Let's test, firstly with a producer, which we can start one of two ways:

```bash
kafka-console-producer --broker-list 127.0.0.1:9092 --topic many-reps
```

or

```bash
$ kafka-console-producer --broker-list 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094 --topic many-reps
>first
>second
>third
```

Now, the only difference is that the 2nd way is safer (and would be the way to do things in production).
It doesn't matter if you only mention one broker (or more), because you are essentially connecting the producer to the cluster.
However, you need a starting point - but if that starting point (broker) is down, and you haven't mentioned any other possible brokers, then the producer fails to connect to the cluster.
By mentioning more than one broker, if the first in the list is down, then the next in the list is tried until either the producer connects to the cluster or it doesn't as all listed brokers are exhausted.

As part of the test, stop the producer and restart but only list one broker:

```bash
$ kafka-console-producer --broker-list 127.0.0.1:9094 --topic many-reps
>94 first
>94 second
>94 third
```

Consume:

```bash
$ kafkacat -C -b 127.0.0.1:9093 -t many-reps -o beginning
94 third
first
% Reached end of topic many-reps [2] at offset 1
second
94 first
% Reached end of topic many-reps [1] at offset 1
third
94 second
% Reached end of topic many-reps [0] at offset 0
% Reached end of topic many-reps [5] at offset 2
% Reached end of topic many-reps [4] at offset 2
% Reached end of topic many-reps [3] at offset 0
```

When finished, shut everything down including Zookeeper:

```bash
$ brew services stop zookeeper
```