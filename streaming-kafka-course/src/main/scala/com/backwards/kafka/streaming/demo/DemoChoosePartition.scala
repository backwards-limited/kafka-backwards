package com.backwards.kafka.streaming.demo

import java.util.concurrent.TimeUnit.SECONDS
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import com.backwards.collection.MapOps._
import com.backwards.kafka.streaming.Config._
import com.backwards.kafka.streaming.demo.Geo._
import com.backwards.text.StringOps._
import com.backwards.time.DurationOps._
import com.typesafe.scalalogging.LazyLogging

trait DemoChoosePartition extends App with LazyLogging {
  val topic: String = "demo-choose-partition"

  val clientId: String = lowerKebab(getClass)

  val partitionCount = 4

  val replicationFactor = 1

  val kafkaProps = {
    val kafkaProps: Map[String, String] =
      load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> clientId)

    val admin: AdminClient = AdminClient create kafkaProps
    admin createTopics asJavaCollection(Seq(new NewTopic(topic, partitionCount, replicationFactor.toShort)))

    kafkaProps
  }
}

abstract class ConsumerDemoChoosePartition extends DemoChoosePartition {
  val consumerProps = kafkaProps + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[Nothing, String](consumerProps)
  sys addShutdownHook consumer.close
  consumer subscribe asJavaCollection(Seq(topic))

  while (true) {
    (consumer poll 10.seconds).iterator.asScala.foreach(println)
  }
}

object ConsumerDemoChoosePartition1 extends ConsumerDemoChoosePartition

object ConsumerDemoChoosePartition2 extends ConsumerDemoChoosePartition

/**
  * - IP in USA will go to partition 0.
  * - Other ips will go to the other partitions.
  * - An error will go to the "error" topic
  */
object ProducerDemoChoosePartition extends DemoChoosePartition {
  val producer = new KafkaProducer[String, String](kafkaProps)
  sys addShutdownHook producer.close

  while (true) {
    val ip = randomIp
    val message = randomString(10)

    val record = geo(ip) match {
      case Left(e) =>
        new ProducerRecord[String, String](s"$topic-error", ip, s"$message: Error = $e")

      case Right(g: Geo) if g.Country.IsoCode == "US" =>
        new ProducerRecord[String, String](topic, 0, ip, message)

      case Right(isoCode) =>
        new ProducerRecord[String, String](topic, isoCode.hashCode % partitionCount + 1, ip, message)
    }

    println(s"Producing: $record")
    producer send record
    SECONDS sleep 10
  }
}