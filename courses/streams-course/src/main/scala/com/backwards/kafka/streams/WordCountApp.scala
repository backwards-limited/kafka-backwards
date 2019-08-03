package com.backwards.kafka.streams

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import com.backwards.collection.MapOps._

object WordCountApp extends App {
  val properties = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "word-count",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
    StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG -> classOf[Serdes.StringSerde],
    StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG -> classOf[Serdes.StringSerde]
  )
}