# Beginners

## Build and Execute

- Unit test
    
  ```bash
  sbt test

  OR

  sbt beginners-course/test
  ```
    
- Integration test
    
  ```bash
  sbt it:test

  OR

  sbt beginners-course/it:test
  ```
    
- Run Application

  As mentioned, run everything from the root of this project as there is only one top-level **build.sbt**
    
  ```bash
  sbt assembly
  docker-compose up --build

  OR

  sbt dockerComposeUp

  OR

  sbt dockerComposeServicesUp
  sbt beginners-course/run
  ```
  
  Finally
  
  ```bash
  docker-compose down

  OR

  sbt dockerComposeDown
  ```
  
- Run Applciation with a Given Environment

  An example of an **environment** file (a hidden file) to interact with managed Elasticsearch on [Bonsai](https://bonsai.io):
  
  ```properties
  ELASTIC_SEARCH_BOOTSTRAP_SERVERS.0=https://<host>:443
  ELASTIC_SEARCH_CREDENTIALS_USER=<user>
  ELASTIC_SEARCH_CREDENTIALS_PASSWORD=<password>
  ```
  
  To use said environment for the running application, provide the system property **environment** upon running:
  
  ```bash
  sbt -DENV=.env beginners-course/run
  ```
  
  where the **.env** file must be in the root of the running module.
 
  Choosing **.env** keeps in line with **docker-compose** environment variables.
  
- Kafka

  While running, take a look at consumer information provided by Kafka:
  
  ```bash
  $ kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group twitter-group-1 --describe
  
  TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG   CONSUMER-ID      HOST            CLIENT-ID
  twitter-topic   0          131             217             86    consumer-1-...   /172.28.0.1     consumer-1
  ```