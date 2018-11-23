# Booting Kafka

## Homebrew

```bash
$ brew services start zookeeper
$ brew services start kafka

$ brew services stop kafka
$ brew services stop zookeeper
```

and where are the associated configurations?

```bash
$ ls -las /usr/local/etc/kafka
...
16 -rw-r--r--   1 davidainslie  admin  5685  8 Oct 16:55 server.properties
...
```