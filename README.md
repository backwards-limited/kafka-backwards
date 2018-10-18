# Kafka by Backwards

Scala module with reusable Kafka functionality (including some Java friendliness).

Example usage included while covering "beginner" to "advanced" Kafka courses.



## Setup
Apologies I shall only cover **Mac** - One day I may include Linux and Windows.

Install [Homebrew](https://brew.sh) for easy package management on Mac:

```bash
$ /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

Install some essentials:

```
$ brew update && \
  brew install scala && \
  brew install sbt && \
  brew install kubernetes-cli && \
  brew install kubectl && \
  brew cask install virtualbox docker minikube
```

As well as the above essentials, we can also install the following to aid testing/understanding:

```
$ brew install kafka && \
  brew install kafkacat && \
  brew install elasticsearch
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
    Take a look at file [docker-compose.yml](docker-compose.yml) to see the use of the **build** directive and comments showing the alternative of using **image**. Whenever code is changed then use the **build** option when running **docker-compose** - this will in turn utilise the **Dockerfile** to build a new image i.e. this approach allows us to always run against the **latest** without having to include the separate step of building a Docker image.



## Kafka

- Zookeeper and Kafka, having been installed with [Homebrew](https://brew.sh), can be started and stopped as [Homebrew](https://brew.sh) services e.g. for testing:

  ``` bash
  $ brew services start zookeeper
  $ brew services start kafka
  ```

  ```bash
  $ brew services stop kafka
  $ brew services stop zookeeper
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



## Elasticsearch

For a managed Elasticsearch, take a look at [bonsai](https://bonsai.io/)

- Elasticsearch, having been installed with [Homebrew](https://brew.sh), can be started and stopped as a [Homebrew](https://brew.sh) service e.g. for testing:

  ```bash
  $ brew services start elasticsearch
  ```

  ```bash
  $ brew services stop elasticsearch
  ```

Elasticsearch can be a tad slow at "warming up". Keep this in mind when testing.

For instance, because the slow start up I ditched the use of [elastic4s](https://sksamuel.github.io/elastic4s) for [elastic-scala-httpclient](https://github.com/bizreach/elastic-scala-httpclient). Now, even though **elastic4s** should be the bees knees for Scala interacting with Elasticsearch, unfortunately the documentation is not obvious when it comes to "connection timeout" and "retries". However, **elastic-scala-httpclient** immediately get to the point and allows this to be easily handled in code.

- With the system running (via docker-compose up), test Elasticsearch with a curl:
    ```bash
    $ curl http://0.0.0.0:9200/twitter/_search?q=user:kimchy
    ```



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



