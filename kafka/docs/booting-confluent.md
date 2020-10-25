# Confluent Booting Kafka

We can manage Kafka with the [confluent-cli](https://github.com/confluentinc/confluent-cli).
Before installing this, we need the [confluent-oss](https://www.confluent.io/download/).
Now there is a homebrew formula for this *oss** using the command **brew install confluent-oss**, but this will clash with the homebrew installation of Kafka.
You could **unlink** the original installation, but instead we shall install the **oss** without homebrew from the link above.

- So, download and extract [confluent-oss](https://www.confluent.io/download/) to somewhere such as your **Applications** folder.

- Add CONFLUENT_HOME environment variable to your **profile** to point to the location of Confluent OSS e.g. in my **.zshrc**

  ```bash
  export CONFLUENT_HOME=/Applications/confluent-5.0.1
  export PATH=${CONFLUENT_HOME}/bin:${PATH}
  ``` 

- Git clone the [confluent-cli](https://github.com/confluentinc/confluent-cli) e.g.

  ```bash
  git clone git@github.com:confluentinc/confluent-cli.git  
  ```
  
- Install confluent-cli

  ```bash
  cd confluent-cli; make install
  ```  

One downside at this moment in time, is that the confluent platform  doesn't support Java beyond 1.8.

As I use [jenv](http://www.jenv.be/) it is easy to select a Java environment first e.g.

```bash
$ jenv versions
    system
    1.8.0.192
    11.0.1
    
$ jenv local 1.8.0.192
```

```bash
$ confluent start
This CLI is intended for development only, not for production
https://docs.confluent.io/current/cli/index.html

Using CONFLUENT_CURRENT: /var/folders/h7/qbkgx9zn0nq222xx5pgh6t6r0000gn/T/confluent.4eBi6pnU
Starting zookeeper
zookeeper is [UP]
Starting kafka
kafka is [UP]
Starting schema-registry
schema-registry is [UP]
Starting kafka-rest
kafka-rest is [UP]
Starting connect
connect is [UP]
Starting ksql-server
ksql-server is [UP]
```

```bash
$ kafka-topics --zookeeper 127.0.0.1:2181 --list
__confluent.support.metrics
__consumer_offsets
_confluent-ksql-default__command_topic
_schemas
connect-configs
connect-offsets
connect-statuses 
```

```bash
$ confluent status
This CLI is intended for development only, not for production
https://docs.confluent.io/current/cli/index.html

ksql-server is [UP]
connect is [UP]
kafka-rest is [UP]
schema-registry is [UP]
kafka is [UP]
zookeeper is [UP]
```

To open the log file:

```bash
$ confluent log connect
```

Runtime stats:

```bash
$ confluent top kafka
```

Available connectors:

```bash
$ confluent list connectors
  This CLI is intended for development only, not for production
  https://docs.confluent.io/current/cli/index.html
  
  Bundled Predefined Connectors (edit configuration under etc/):
    elasticsearch-sink
    file-source
    file-sink
    jdbc-source
    jdbc-sink
    hdfs-sink
    s3-sink
```

Stop and destroy (which deletes data and logs):

```bash
$ confluent stop

$ confluent destroy
```