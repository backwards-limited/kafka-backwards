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
  
  However, here we have a **gotcha**.
  A Docker image is build from the latest **assembly JAR**, and if you don't first build an **assembly JAR** you will miss out on the latest changes.
  
  So we really should do the following:
  
  ```bash
  sbt clean assembly
  
  docker-compose up --build
  ```
  
  Now, **sbt** does have a workaround for this by using plugins such as [sbt-docker](https://github.com/marcuslonnberg/sbt-docker) and [sbt-docker-compose](https://github.com/Tapad/sbt-docker-compose).
  However, these rely on the fact that you do not write a standard **Dockerfile** and instead write one in the [build.sbt](../build.sbt).
  I've avoided using these plugins for several reasons (stories for another day).
  
  The plugins mentioned provide various sbt tasks to execute required Docker and Compose commands.
  As a convenience, since I'm avoiding the these plugins, I've added a few tasks (more to be added as required) to mimic the plugins, where these tasks merely wrap commands you would normally provided.
  
  ```bash
  sbt dockerComposeUp

  sbt dockerComposeDown
  ```