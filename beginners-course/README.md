# Beginners Course

Using Backwards Kafka functionality and following the excellent [Kafka Beginners Course](https://www.udemy.com/apache-kafka/learn/v4/overview).

Note that this module is within a multi-module project and that there is only one top level build (build.sbt).

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
  ELASTIC_SEARCH_BOOTSTRAP_SERVERS.0 = https://<host>:443
  ELASTIC_SEARCH_CREDENTIALS_USER = <user>
  ELASTIC_SEARCH_CREDENTIALS_PASSWORD = <password>
  ```
  
  To use said environment for the running application, provide the system property **environment** upon running:
  
  ```bash
  sbt -Denvironment=.environment beginners-course/run
  ```
  
  where the **.environment** file must be in the root of the running module.
 