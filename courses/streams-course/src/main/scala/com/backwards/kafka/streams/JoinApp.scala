package com.backwards.kafka.streams

import wvlet.log.LazyLogger
import com.backwards.kafka.admin.KafkaAdmin

/**
  * Join user purchases (KStream) to user data (GlobalKTable).
  * Write a producer to explain the different scenarions.
  * Observe the output.
  */
object JoinApp extends App with KafkaAdmin with LazyLogger {

}