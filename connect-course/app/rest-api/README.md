# Kafka Connect REST API

As always, We need a Kafka cluster - In root of this project run the [docker-compose.yml](../../docker-compose.yml):

```bash
$ docker-compose up kafka-cluster
```

And then start up landoop with all necessary tools:

```bash
$ docker run --rm -it --net=host landoop/fast-data-dev bash

# Install jq to pretty print JSON
root@fast-data-dev / $ apk update && apk add jq
```

Let's hit some REST endpoints.

## Get worker information

```bash
root@fast-data-dev / $ curl -s 127.0.0.1:8083 | jq
{
  "version": "1.1.1-L0",
  "commit": "4dae083af486eaed",
  "kafka_cluster_id": "cpunp8DST36CpOtbncrvdg"
}
```

## List connectors available on a worker

```bash
root@fast-data-dev / $ curl -s 127.0.0.1:8083/connector-plugins | jq
{
  "version": "1.1.1-L0",
  "commit": "4dae083af486eaed",
  "kafka_cluster_id": "cpunp8DST36CpOtbncrvdg"
}
root@fast-data-dev / $ curl -s 127.0.0.1:8083/connector-plugins | jq
[
  {
    "class": "com.couchbase.connect.kafka.CouchbaseSinkConnector",
    "type": "sink",
    "version": "3.2.2"
  },
  {
    "class": "com.couchbase.connect.kafka.CouchbaseSourceConnector",
    "type": "source",
    "version": "3.2.2"
  },
  ...
```

## Ask about active connectors

```bash
root@fast-data-dev / $ curl -s 127.0.0.1:8083/connectors | jq
[
  "sink-elastic-twitter-distributed",
  "source-twitter-distributed",
  "logs-broker"
]
```

## Get information about a connector tasks and config

```bash
root@fast-data-dev / $ curl -s 127.0.0.1:8083/connectors/logs-broker/tasks | jq
[
  {
    "id": {
      "connector": "logs-broker",
      "task": 0
    },
    "config": {
      "file": "/var/log/broker.log",
      "task.class": "org.apache.kafka.connect.file.FileStreamSourceTask",
      "batch.size": "2000",
      "topic": "logs_broker"
    }
  }
]
```

## Get connector status

```bash
root@fast-data-dev / $ curl -s 127.0.0.1:8083/connectors/logs-broker/status | jq
{
  "name": "logs-broker",
  "connector": {
    "state": "RUNNING",
    "worker_id": "127.0.0.1:8083"
  },
  "tasks": [
    {
      "state": "RUNNING",
      "id": 0,
      "worker_id": "127.0.0.1:8083"
    }
  ],
  "type": "source"
}
```

## Pause / Resume a connector (no response if the call is successful)

```bash
root@fast-data-dev / $ curl -s -X PUT 127.0.0.1:8083/connectors/logs-broker/pause

root@fast-data-dev / $ curl -s -X PUT 127.0.0.1:8083/connectors/logs-broker/resume
```

## Get connector configuration

```bash
root@fast-data-dev / $ curl -s 127.0.0.1:8083/connectors/logs-broker | jq
{
  "name": "logs-broker",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "file": "/var/log/broker.log",
    "tasks.max": "1",
    "name": "logs-broker",
    "topic": "logs_broker"
  },
  "tasks": [
    {
      "connector": "logs-broker",
      "task": 0
    }
  ],
  "type": "source"
}
```

## Delete our connector

```bash
root@fast-data-dev / $ curl -s -X DELETE 127.0.0.1:8083/connectors/twitter-source-distributed
```

## Create a new connector

```bash
root@fast-data-dev / $ curl -s -X POST -H "Content-Type: application/json" --data '{ "name": "file-stream-distributed", "config": { "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector", "key.converter.schemas.enable": "true", "file": "demo-file.txt", "tasks.max": "1", "value.converter.schemas.enable": "true", "name": "file-stream-distributed", "topic": "demo-distributed", "value.converter": "org.apache.kafka.connect.json.JsonConverter", "key.converter": "org.apache.kafka.connect.json.JsonConverter" } }' http://127.0.0.1:8083/connectors | jq

{
  "name": "file-stream-distributed",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "key.converter.schemas.enable": "true",
    "file": "demo-file.txt",
    "tasks.max": "1",
    "value.converter.schemas.enable": "true",
    "name": "file-stream-distributed",
    "topic": "demo-distributed",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter"
  },
  "tasks": [],
  "type": null
}
```

## Update a connector configuration

```bash
root@fast-data-dev / $ curl -s -X PUT -H "Content-Type: application/json" --data '{ "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector", "key.converter.schemas.enable": "true", "file": "demo-file.txt", "tasks.max": "2", "value.converter.schemas.enable": "true", "name": "file-stream-distributed", "topic": "demo-distributed", "value.converter": "org.apache.kafka.connect.json.JsonConverter", "key.converter": "org.apache.kafka.connect.json.JsonConverter" }' http://127.0.0.1:8083/connectors/file-stream-distributed/config | jq

{
  "name": "file-stream-distributed",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "key.converter.schemas.enable": "true",
    "file": "demo-file.txt",
    "tasks.max": "2",
    "value.converter.schemas.enable": "true",
    "name": "file-stream-distributed",
    "topic": "demo-distributed",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter"
  },
  "tasks": [
    {
      "connector": "file-stream-distributed",
      "task": 0
    }
  ],
  "type": "source"
}
```

