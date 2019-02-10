# Twitter Connector

> ![Goal](docs/images/goal.png)

We need a Kafka cluster - In root of this project run the [docker-compose.yml](../../docker-compose.yml):

```bash
$ docker-compose up kafka-cluster
```

Now need a topic, so run:

```bash
$ docker run --rm -it --net=host landoop/fast-data-dev bash

root@fast-data-dev / $ kafka-topics --zookeeper 127.0.0.1:2181 --create --topic my-twitter --partitions 3 --replication-factor 1
Created topic "my-twitter".
```

And start a consumer on said topic (as an observer):

```bash
root@fast-data-dev / $ kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic my-twitter
```

At this point, go to the Kafka Connect UI in the [browser](http://127.0.0.1:3030/kafka-connect-ui/#/cluster/fast-data-dev):

> ![Connect UI](docs/images/kafka-connect-ui.png)

---

> ![Twitter connect](docs/images/twitter-connect.png)

And provide our properties from [source-twitter-distributed.properties](source-twitter-distributed.properties).

