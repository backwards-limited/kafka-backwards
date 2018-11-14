package com.backwards

import org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.{ACKS_CONFIG, ENABLE_IDEMPOTENCE_CONFIG, MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION}
import com.backwards.config.ConfigOps
import com.backwards.logging.Logging

package object kafka extends ConfigOps with Logging {
  lazy val config: KafkaConfig = {
    val c = load[KafkaConfig]("kafka") +
      (ENABLE_IDEMPOTENCE_CONFIG -> "true") +
      (ACKS_CONFIG -> "all") +
      (RETRIES_CONFIG -> Int.MaxValue.toString) +
      (MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION -> "5")

    info(s"Kafka configuration: $c")
    c
  }
}