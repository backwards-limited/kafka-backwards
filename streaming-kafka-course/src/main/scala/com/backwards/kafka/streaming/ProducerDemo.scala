package com.backwards.kafka.streaming

import com.backwards.kafka.streaming.Config._
import com.typesafe.scalalogging.LazyLogging

object ProducerDemo extends App with LazyLogging {
  val kafkaConfig: KafkaConfig = load[KafkaConfig]("kafka")
}