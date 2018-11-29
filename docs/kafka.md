# Kafka

- Zookeeper and Kafka, having been installed with [Homebrew](https://brew.sh), can be started and stopped as [Homebrew](https://brew.sh) services e.g. for testing:

``` bash
brew services start zookeeper
brew services start kafka
```

```bash
brew services stop kafka
brew services stop zookeeper
```

- When Kafka and Zookeeper are up, [kafkacat](https://github.com/edenhill/kafkacat) can be used to send/receive (for testing) e.g. to send:

```bash
$ kafkacat -P -b 127.0.0.1 -t test-topic
typing text and hitting enter to send message to test-topic 
and again typing text and hitting enter to send message to test-topic 
```

- and to receive:
  
```bash
$ kafkacat -C -b 127.0.0.1 -t test-topic
... here we will see consumed messages
```

## Guidelines

### Partitions

How many for a topic?

- < 6 brokers choose twice number of brokers e.g. 5 brokers => 10 partitions per topic

  Why more? A small cluster will probably grow so be in a position to handle that.

- \> 12 brokers choose same number e.g. 15 broker => 15 partitions per topic

  Why? A large cluster probably will not grow that much.

A broker should not hold more than between 2000 to 4000 partitions (across all topics of said broker)l

A cluster should not exceed 20,000 partitions across all brokers.

### Replication Factor

Usually start with 3 (which is the same as the minimum recommended brokers in a cluster).

Maximum will probably only be 4.