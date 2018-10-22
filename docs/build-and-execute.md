# Building and Executing

- The project image can be build with:

  ```bash
  docker build -t backwards/kafka .
  ```
  
- Build and execute using the following:

  ```bash
  docker-compose up --build
  ```
  
  Take a look at file [docker-compose.yml](docker-compose.yml) to see the use of the **build** directive and comments showing the alternative of using **image**.
  Whenever code is changed then use the **build** option when running **docker-compose** - this will in turn utilise the **Dockerfile** to build a new image.
  i.e. this approach allows us to always run against the **latest** without having to include the separate step of building a Docker image.