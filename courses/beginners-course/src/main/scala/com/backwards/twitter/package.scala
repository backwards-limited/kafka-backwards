package com.backwards

import org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG
import com.backwards.config.Config
import com.backwards.kafka.KafkaConfig

package object twitter extends Config {
  lazy val kafkaConfig: KafkaConfig = {
    val c = load[KafkaConfig]("kafka") + (RETRIES_CONFIG -> Int.MaxValue) // TODO - Can we configure Int.MaxValue?

    scribe info s"Kafka configuration: $c"
    c
  }
}