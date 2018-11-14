package com.backwards

import org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG
import com.backwards.config.ConfigOps
import com.backwards.logging.Logging

package object kafka extends ConfigOps with Logging {
  lazy val config: KafkaConfig = {
    val c = load[KafkaConfig]("kafka") + (RETRIES_CONFIG -> Int.MaxValue) // TODO - Can we configure Int.MaxValue?

    info(s"Kafka configuration: $c")
    c
  }
}