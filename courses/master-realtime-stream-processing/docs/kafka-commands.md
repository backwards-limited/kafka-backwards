# Kafka Commands

With Zookeeper and Kafka cluster running (use [docker-compose.yml](../kafka/docker/docker-compose.yml)) ...

We'll first check brokers connected to Zookeeper:
```bash
➜ zookeeper-shell localhost:2181
Connecting to localhost:2181
Welcome to ZooKeeper!
JLine support is disabled

WATCHER::

WatchedEvent state:SyncConnected type:None path:null

ls /
[admin, brokers, cluster, config, consumers, controller, controller_epoch, isr_change_notification, latest_producer_id_block, log_dir_event_notification, zookeeper]

ls /brokers
[ids, seqid, topics]

ls /brokers/ids
[0, 1, 2]

get /controller
{"version":1,"brokerid":2,"timestamp":"1602004464736"}
```

## List Topics

```bash
kafka-topics --list --bootstrap-server localhost:9092
```

> We show commands from outside Docker, but we can execute within Docker using the **kafka** container.
> In fact we have two versions - This first one executes commands within the Docker network:
> ```bash
> docker exec -it kafka-tools-inside bash
> 
> kafka-topics --list --bootstrap-server kafka0:19092
> ```
> This second one executes commands from your host:
> ```bash
> docker exec -it kafka-tools-outside bash
> 
> kafka-topics --list --bootstrap-server localhost:9092
> ```

## Create Topic

```bash
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic invoice
```

## Consume with Kafkacat

```bash
kafkacat -C -b localhost:9092 -t some-topic -o beginning
```