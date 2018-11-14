# Kafka Streams
  
## Application

Take a look at com.backwards.twitter.stream.TwitterRunner

As mentioned, run everything from the root of this project as there is only one top-level **build.sbt**.

TODO - MOVE DOCKER COMPOSE FILES INTO RELVANT MODULES

We are taking a simple approach to running this application with a given environment.

First start up required services with:

```bash
sbt dockerComposeServicesUp
```

Then run and select the desired application:

```bash
sbt -DENV=.env beginners-course/run
```

where the **.env** file must be in the root of the running module.

Choosing **.env** keeps in line with **docker-compose** environment variables.

Finally

```bash
sbt dockerComposeDown
```

## Kafka

To check (before shutting down) that Kafka Streams has placed **tweets** onto our chosen topic, run:

```bash
kafkacat -C -b 127.0.0.1 -t important-tweets-topic
```