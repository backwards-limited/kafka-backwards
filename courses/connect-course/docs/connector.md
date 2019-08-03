# Connector

## Config

Config Def is the way to communicate to the user how you want your configuration to be. There can be **mandatory** or **optional** with defaults along with **validation** of **types** and **constraints**. e.g.

```scala
val ownerConfig = "github.owner"
val ownerDoc = "Owner of the repository you would like to follow"

val conf = (new ConfigDef)
  .define(ownerConfig, Type.String, Importance.HIGH, ownerDoc)
  .define(batchSizeConfig, Type.Int, 100, new BatchSizeValidator, Importance.LOW, batchSizeDoc)
```

## Class

Load config and create tasks. Implement the following:

- version
- start
- taskClass
- taskConfigs
- stop
- config