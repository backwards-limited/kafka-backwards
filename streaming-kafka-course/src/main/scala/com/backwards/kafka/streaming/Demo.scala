package com.backwards.kafka.streaming

import java.time.Duration
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.ConsumerRecords
import com.backwards.collection.MapOps._
import com.backwards.kafka.streaming.Config._
import com.typesafe.scalalogging.LazyLogging

trait Demo extends App with LazyLogging {
  val topic: String = "test-topic"
}

object ConsumerDemo extends Demo {
  import org.apache.kafka.clients.consumer.ConsumerConfig.{CLIENT_ID_CONFIG, GROUP_ID_CONFIG}
  import org.apache.kafka.clients.consumer.KafkaConsumer

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> "consumer-demo") + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[String, String](kafkaProps)
  sys.addShutdownHook(consumer.close())
  consumer subscribe asJavaCollection(Seq(topic))

  while (true) {
    val records: ConsumerRecords[String, String] = consumer poll Duration.ofSeconds(10)

    records.iterator().asScala.foreach(println)
  }
}

object ProducerDemo extends Demo {
  import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
  import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> "producer-demo")

  val producer = new KafkaProducer[String, String](kafkaProps)
  val record = new ProducerRecord[String, String](topic, "my-key", "my-value")

  producer send record

  producer.close()
}