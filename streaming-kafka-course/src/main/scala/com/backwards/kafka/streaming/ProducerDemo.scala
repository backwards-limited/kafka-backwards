package com.backwards.kafka.streaming

import monocle.macros.syntax.lens._
import org.apache.kafka.clients.producer.ProducerConfig.{CLIENT_ID_CONFIG, KEY_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import com.backwards.kafka.streaming.Config._
import com.typesafe.scalalogging.LazyLogging

object ProducerDemo extends App with LazyLogging {
  val kafkaConfig = load[KafkaConfig]("kafka").lens(_.props).modify {
    _ + (CLIENT_ID_CONFIG -> "producer-demo") + (KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer].getName) + (VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer].getName)
  }

  val producer = new KafkaProducer[String, String](kafkaConfig.toProperties)

  val record = new ProducerRecord[String, String]("test-topic", "my-key", "my-value")

  producer.send(record)

  producer.close()
}