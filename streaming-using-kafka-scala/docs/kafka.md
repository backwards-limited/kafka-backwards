# Kafka

Make sure kafka is up and running via [docker-compose.yml](../src/main/resources/docker-compose.yml).

## Commands

#### Create topic

```bash
$ kafka-topics --zookeeper localhost:2181 --partitions 1 --replication-factor 3 --create --topic test 
Created topic "test".
```

#### List topics

```bash
$ kafka-topics --zookeeper localhost:2181 --list
__confluent.support.metrics
__consumer_offsets
_schemas
test
```

#### Describe topic

```bash
$ kafka-topics --zookeeper localhost:2181 --describe --topic test
Topic:test      PartitionCount:1        ReplicationFactor:3     Configs:
        Topic: test     Partition: 0    Leader: 1       Replicas: 1,3,2 Isr: 1,3,2
```

#### Produce messages

```bash
$ kafka-console-producer --broker-list localhost:9092,localhost:9093 --topic test
>Hello World
>Multiple node kafka cluster running
>Producing messages
>
```

#### Consume messages

```bash
$ kafka-console-consumer --bootstrap-server localhost:9092,localhost:9093 --topic test --from-beginning
Hello World
Multiple node kafka cluster running
Producing messages

```

#### Produce stream of messages

```bash
$ kafka-topics --zookeeper localhost:2181 --partitions 3 --replication-factor 2 --create --topic streaming
Created topic "streaming".

$ kafka-topics --zookeeper localhost:2181 --describe --topic streaming                               
Topic:streaming PartitionCount:3        ReplicationFactor:2     Configs:
        Topic: streaming        Partition: 0    Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: streaming        Partition: 1    Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: streaming        Partition: 2    Leader: 3       Replicas: 3,2   Isr: 3,2
```

With our new topic created, execute the [streaming](../src/main/resources/kafka/streaming.sh) script with the new topic name:

```bash
$ ./streaming.sh streaming
Mockaroo data -> Topic streaming
Q or q and Enter to Quit
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    55    0    55    0     0     30      0 --:--:--  0:00:01 --:--:--    30>
>  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    62    0    62    0     0     84      0 --:--:-- --:--:-- --:--:--    84>
>  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    56    0    56    0     0     77      0 --:--:-- --:--:-- --:--:--    77
>>  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    60    0    60    0     0     91      0 --:--:-- --:--:-- --:--:--    91
```

#### Consume stream of messages

```bash
$ kafka-console-consumer --bootstrap-server localhost:9092,localhost:9093 --topic streaming --from-beginning
1,Julee,Devenport,jdevenport0@chron.com,Female,132.195.254.62
1,Nicoline,Dome,ndome0@phoca.cz,Female,158.106.164.188
1,Bernie,Gynni,bgynni0@arizona.edu,Female,55.141.219.98
1,Antonetta,Newens,anewens0@boston.com,Female,12.230.108.41
1,Lexine,Allbut,lallbut0@mlb.com,Female,62.120.148.16
1,Roddie,Raddish,rraddish0@salon.com,Male,144.140.227.46
```

