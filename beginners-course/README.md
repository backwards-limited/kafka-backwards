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