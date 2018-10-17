# Kafka by Backwards

Scala module with reusable Kafka functionality (including some Java friendliness).<br/>
Example usage included while covering "beginner" to "advanced" Kafka courses.

## Setup
On Mac:
```
$ brew update && \
  brew install scala && \
  brew install sbt && \
  brew install kubernetes-cli && \
  brew install kubectl && \
  brew cask install virtualbox docker minikube
```

## TODO

- Extract module **backwards** into its own repo e.g. backwards

## Building and Executing

A small caveat: Various approaches have been taken with the use of sbt, docker and docker-compose that are not efficient.
As a result, many things that are automated, take longer than necessary.
These approaches have been taken for simplicity (over efficiency) purely from an educational perspective.

- The project image can be build with:
    ```bash
    $ docker build -t backwards/kafka .
    ```
  
- Build and execute using the following:
    ```bash
    $ docker-compose up --build
    ```
    Take a look at file [docker-compose.yml](docker-compose.yml) to see the use of the **build** directive and comments showing the alternative of using **image**
  
## Kafka

- When Kafka (and Zookeeper) are up, **kafkacat** can be used to send/receive (for testing) e.g. to send:
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

## Elasticsearch

For a managed Elasticsearch, take a look at [bonsai](https://bonsai.io/)

## Modules

### Beginners Course

Using Backwards Kafka functionality and following the excellent [Kafka Beginners Course](https://www.udemy.com/apache-kafka/learn/v4/overview)

- Unit test
    ```bash
    $ sbt beginners-course/test
    ```
    
- Integration test
    ```bash
    $ sbt beginners-course/it:test
    ```
    
- Application
    ```bash
    $ docker-compose up --build
    ```