package com.backwards.kafka.streaming

final case class KafkaConfig(zookeeper: String, bootstrap: Bootstrap)

final case class Bootstrap(server: String)