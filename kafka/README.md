# Kafka

## Useful Commands

- Topics list

  ```bash
  kafka-topics --zookeeper 127.0.0.1:2181 --list
  ```

- Consumer group description

  ```bash
  $ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
    Consumer group 'twitter-group-1' has no active members.
    
    TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
    twitter-topic   0          216             308             92              -               -               -
  ```
  
- Reset offsets so consumer can replay events

  ```bash
  $ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --topic twitter-topic --group twitter-group-1 --reset-offsets --execute --to-earliest
  
  TOPIC                          PARTITION  NEW-OFFSET
  twitter-topic                  0          0

  $ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
  Consumer group 'twitter-group-1' has no active members.
  
  TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
  twitter-topic   0          0               308             308             -               -               -

  $ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
    
  TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
  twitter-topic   0          20              374             354             consumer-1-8dbd81ed-ee53-40f9-a414-3c54918cbfdf /172.31.0.1     consumer-1
  ```  

## Consumer Strategies

- Auto Commit On - Default

  enable.auto.commit = true
  and
  Synchronous processing of batches i.e. only **poll** after processing current batch (that was fetched).
  
  With auto commit, offsets will be committed automatically for you at a regular interval (by default **auto.commit.interval.ms = 5000**) everytime you call **poll()**.
  
  If you don't use synchronous processing, you will be in **at most once** behaviour because offsets will be committed before data is processed - **which can be dangerous**.
  
- Auto Commit Off

  enable.auto.commit = false
  and so
  Manually commit offsets.