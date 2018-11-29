# Simple

## Application

Take a look at **com.backwards.twitter.simple.TwitterRunner** (there is an equivalent stream version **com.backwards.twitter.stream.TwitterRunner**).

As mentioned, run everything from the root of this project as there is only one top-level **build.sbt**

```bash
$ sbt "dockerComposeUp beginners-course"
```

OR

```bash
$ sbt "dockerComposeServicesUp beginners-course"

$ sbt beginners-course/run
```

Finally

```bash
$ sbt "dockerComposeDown beginners-course"
```
  
## Run Applciation with a Given Environment

An example of an **environment** file (a hidden file) to interact with managed Elasticsearch on [Bonsai](https://bonsai.io):

```properties
ELASTIC_SEARCH_BOOTSTRAP_SERVERS.0=https://<host>:443
ELASTIC_SEARCH_CREDENTIALS_USER=<user>
ELASTIC_SEARCH_CREDENTIALS_PASSWORD=<password>
```

To use said environment for the running application, provide the system property **environment** upon running e.g.:

```bash
sbt -DENV=beginners-course/.env-local beginners-course/run
```

where the **environment** file must be relative to where you are running the application from (the above shows path and file name).

Choosing **.env** keeps in line with **docker-compose** environment variables. Within the repository, there is a **.env example**.
  
## Kafka

While running, take a look at consumer information provided by Kafka:

```bash
$ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG   CONSUMER-ID      HOST            CLIENT-ID
twitter-topic   0          131             217             86    consumer-1-...   /172.28.0.1     consumer-1
```