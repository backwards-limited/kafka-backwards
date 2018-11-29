# Useful Commands

## Topics List

```bash
kafka-topics --zookeeper 127.0.0.1:2181 --list
```

## Consumer Group Description

```bash
$ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
  
Consumer group 'twitter-group-1' has no active members.
  
TOPIC         PARTITION  CURRENT-OFFSET LOG-END-OFFSET LAG CONSUMER-ID HOST CLIENT-ID
twitter-topic 0          216            308            92  -           -    -
```

## Reset Offsets So Consumer Can Replay Events

```bash
$ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --topic twitter-topic --group twitter-group-1 --reset-offsets --execute --to-earliest

TOPIC                          PARTITION  NEW-OFFSET
twitter-topic                  0          0

$ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
Consumer group 'twitter-group-1' has no active members.

TOPIC         PARTITION CURRENT-OFFSET LOG-END-OFFSET LAG CONSUMER-ID HOST CLIENT-ID
twitter-topic 0         0              308            308 -           -    -

$ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
  
TOPIC         PARTITION CURRENT-OFFSET LOG-END-OFFSET LAG CONSUMER-ID
                                                                HOST        CLIENT-ID
twitter-topic 0         20             374            354 consumer-1-8dbd81ed-ee53-40f9-a414-3c54918cbfdf                                  
                                                                /172.31.0.1 consumer-1
```

## Configure a Topic

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --create --topic my-topic --partitions 3 --replication-factor 1
Created topic "my-topic"

$ kafka-topics --zookeeper 127.0.0.1:2181 --describe --topic my-topic
Topic:my-topic	PartitionCount:3	ReplicationFactor:1	Configs:
  Topic: my-topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
  Topic: my-topic	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
  Topic: my-topic	Partition: 2	Leader: 0	Replicas: 0	Isr: 0
  
$ kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name my-topic --alter --add-config min.insync.replicas=2
Completed Updating config for entity: topic 'my-topic'

$ kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name my-topic --describe
Configs for topic 'my-topic' are min.insync.replicas=2

$ kafka-topics --zookeeper 127.0.0.1:2181 --describe --topic my-topic
Topic:my-topic	PartitionCount:3	ReplicationFactor:1	Configs:min.insync.replicas=2
  Topic: my-topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
  Topic: my-topic	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
  Topic: my-topic	Partition: 2	Leader: 0	Replicas: 0	Isr: 0
  
$ kafka-configs --zookeeper 127.0.0.1:2181 --entity-type topics --entity-name my-topic --alter --delete-config min.insync.replicas
Completed Updating config for entity: topic 'my-topic'	
```

## Configure Log Compaction

Either create a new topic or delete one to start afresh.

Purely for presentation, let's delete a topic (and then recreate):

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --delete --topic my-topic
Topic my-topic is marked for deletion.
Note: This will have no impact if delete.topic.enable is not set to true
```

Interesting. If we indeed want this setting then add it to **/usr/local/etc/kafka/server.properties**. (Don't forget to reboot Kafka).
There is more on "deleting" below.

So, create a topic with log compaction:

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --create --topic my-salary --partitions 1 --replication-factor 1 --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.001 --config segment.ms=5000
Created topic "my-salary"

$ kafka-topics --zookeeper 127.0.0.1:2181 --describe --topic my-salary
Topic:my-salary	PartitionCount:1	ReplicationFactor:1	Configs:min.cleanable.dirty.ratio=0.001,cleanup.policy=compact,segment.ms=5000
	Topic: my-topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```

Start consumer (in one terminal):

```bash
$ kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic my-salary --from-beginning --property print.key=true --property key.separator=,

Mark,salary: 10000
Lucy,salary: 20000
Bob,salary: 15000
Patrick,salary: 30000
Mark,salary: 15000
Patrick,salary: 25000
John,salary: 10000
```

Publish to topic (from another terminal):

```bash
$ kafka-console-producer --broker-list 127.0.0.1:9092 --topic my-salary --property parse.key=true --property key.separator=,
>Mark,salary: 10000
>Lucy,salary: 20000
>Bob,salary: 15000
>Patrick,salary: 30000
>Mark,salary: 15000
>Patrick,salary: 25000
>John,salary: 10000
```

Stop and restart the consumer:

```bash
$ kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic my-salary --from-beginning --property print.key=true --property key.separator=,
Lucy,salary: 20000
Bob,salary: 15000
Mark,salary: 15000
Patrick,salary: 25000
John,salary: 10000
```

Note that compaction is not instantaneous and you may need to restart the consumer a few times to actually witness log compaction.

## Delete Topic

As mentioned deleting a topic will have no impact if delete.topic.enable is not set to true in **server.properties**, then we can do:

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --delete --topic my-topic
Topic my-topic is marked for deletion.
```

However, even this may not work. So, log into the Zookeeper CLI and do the following (where I want to delete topic __confluent.support.metrics):

```bash
$ zkCli
get /brokers/topics/__confluent.support.metrics
rmr /brokers/topics/__confluent.support.metrics
rmr /admin/delete_topics/__confluent.support.metrics
get /brokers/topics/__confluent.support.metrics
```

Log out and double check:

```bash
kafka-topics --zookeeper 127.0.0.1:2181 --list
```