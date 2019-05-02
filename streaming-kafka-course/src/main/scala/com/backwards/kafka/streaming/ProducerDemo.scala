package com.backwards.kafka.streaming

import java.util.Properties
import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import com.backwards.kafka.streaming.Config._
import com.typesafe.scalalogging.LazyLogging

object ProducerDemo extends App with LazyLogging {
  // TODO - Extract to scala-backwards

  implicit val map2Properties: Map[String, String] => Properties =
    m => (new Properties /: m) {
    case (properties, (k, v)) =>
      properties.put(k, v)
      properties
  }

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> "producer-demo")

  val producer = new KafkaProducer[String, String](kafkaProps)

  val record = new ProducerRecord[String, String]("test-topic", "my-key", "my-value")

  producer.send(record)

  producer.close()
}