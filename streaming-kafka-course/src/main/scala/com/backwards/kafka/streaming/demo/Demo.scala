package com.backwards.kafka.streaming.demo

import java.util.concurrent.TimeUnit.SECONDS
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random
import com.backwards.collection.MapOps._
import com.backwards.kafka.streaming.Config._
import com.backwards.time.DurationOps._
import com.typesafe.scalalogging.LazyLogging

trait Demo extends App with LazyLogging {
  val topic: String = "demo"
}

object ConsumerDemo extends Demo {
  import org.apache.kafka.clients.consumer.ConsumerConfig.{CLIENT_ID_CONFIG, GROUP_ID_CONFIG}
  import org.apache.kafka.clients.consumer.KafkaConsumer

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> s"consumer-$topic") + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[String, String](kafkaProps)
  sys addShutdownHook consumer.close()
  consumer subscribe asJavaCollection(Seq(topic))

  while (true) {
    (consumer poll 10.seconds).iterator.asScala.foreach(println)
  }
}

object ProducerDemo extends Demo {
  import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
  import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> s"producer-$topic")

  val producer = new KafkaProducer[String, String](kafkaProps)
  sys addShutdownHook producer.close()

  def randomString(length: Int) = Stream.continually(Random.nextPrintableChar) take length mkString

  while (true) {
    val record = new ProducerRecord[String, String](topic, randomString(2), randomString(10))
    println(s"Producing: $record")
    producer send record
    SECONDS sleep 10
  }
}