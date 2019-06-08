# Building and Executing

## SBT

```bash
$ sbt test

$ sbt it:test
```

## Environment

Any environment variables that should not be saved in **Git** should be in a **.env** file - a hidden file, where [.env example](../.env example) can be used as a template.
If necessary, you may need other **environments** such as **.env-local** and **.env-dev** etc.

## Build Project Image

```bash
docker build -t backwards/kafka .
```
  
## Execute

```bash
docker-compose up --build
```

but, we need a **docker compose** file...
  
Take a look at the **beginners-course** [docker-compose.yml](beginner-course/docker-compose.yml) to see the use of the **build** directive and comments showing the alternative of using **image**.
Whenever code is changed then use the **build** option when running **docker-compose** - this will in turn utilise the **Dockerfile** to build a new image.
i.e. this approach allows us to always run against the **latest** without having to include the separate step of building a Docker image.

However, here we have a **gotcha**.
A Docker image is built from the latest **assembly JAR**, and if you don't first build an **assembly JAR** you will miss out on the latest changes.

So we really should do the following:

```bash
sbt clean assembly

docker-compose up --build
```

i.e. we wish to **build** and **package** before executing via some **docker compose** file.
  
Now, **sbt** does have a workaround for this by using plugins such as [sbt-docker](https://github.com/marcuslonnberg/sbt-docker) and [sbt-docker-compose](https://github.com/Tapad/sbt-docker-compose).
However, these rely on the fact that you do not write a standard **Dockerfile** and instead write one in the [build.sbt](../build.sbt).
I've avoided using these plugins for several reasons (stories for another day).

The plugins mentioned provide various sbt tasks to execute required Docker and Compose commands.
As a convenience, since I'm avoiding the these plugins, I've added a few tasks (more to be added as required in [docker.sbt](../docker.sbt)) to mimic the plugins, where these tasks wrap commands you would normally provided.

Let's go through some examples.
**Note** that working within a **multi module** project, from the **root** (top level) of the project, we need to state which module we are working with.
  
Within a module such as **beginners-course** we need our relevant **docker compose** files and associated **Dockerfile**.

```bash
$ sbt "dockerComposeUp beginners-course"
```

By default, the above will build and package the project, and execute the [docker-compose.yml](beginner-course/docker-compose.yml) within module **beginners-course**.
What if we wish to use a different docker compose file? Let's run [docker-compose-services.yml](beginner-course/docker-compose-services.yml) which is a convenience file to run all necessary 3rd party services utilised by our module:

```bash
$ sbt "dockerComposeUp beginners-course/docker-compose-services.yml"
```

or use the **-f** option that can be used with **docker compose** when stating the specific file to run with:

```bash
$ sbt "dockerComposeUp -f beginners-course/docker-compose-services.yml"
```

Now on this subject, since starting up required services can be useful e.g. to start up (or even debug) an application separately, we have another **sbt task** that again just acts on the given module:

```bash
$ sbt "dockerComposeServicesUp beginners-course"
```

Finally, to bring down docker, we can either change into the relevant directory and run:

```bash
$ docker-compose down
```

or use another associated **sbt task**:

```bash
$ sbt "dockerComposeDown beginners-course"
```

## An Application
  
To simply use the ubiquitous **run** (where again we indicate the desired module because as mentioned this is a multi module project):
  
```bash
sbt <module>/run
```

e.g.

```bash
sbt beginners-course/run
```

And if your remember, the first thing we mentioned above, was **environment**. Let's run our application with an environment:

```bash
sbt -DENV=beginners-course/.env-local beginners-course/run
```