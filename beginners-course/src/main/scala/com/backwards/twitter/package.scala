package com.backwards

import org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG
import com.backwards.config.Config
import com.backwards.kafka.KafkaConfig
import com.backwards.logging.Logging

package object twitter extends Config with Logging {
  lazy val kafkaConfig: KafkaConfig = {
    val c = load[KafkaConfig]("kafka") + (RETRIES_CONFIG -> Int.MaxValue) // TODO - Can we configure Int.MaxValue?

    info(s"Kafka configuration: $c")
    c
  }
}