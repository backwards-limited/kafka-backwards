# Beginners Course

Using Backwards Kafka functionality and following the excellent [Kafka Beginners Course](https://www.udemy.com/apache-kafka/learn/v4/overview).

Note that this module is within a multi-module project and that there is only one top level build (build.sbt).
It is expected that building and executing is done at the top level where a necessary module is provided i.e.

instead of
```bash
sbt test
```

issue
```bash
sbt module/test
```

## Build and Execute

- Unit test
    
  ```bash
  sbt beginners-course/test
  ```
    
- Integration test
    
  ```bash
  sbt beginners-course/it:test
  ```
    
- Application
    
  ```bash
  docker-compose up --build
  ```