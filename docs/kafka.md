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