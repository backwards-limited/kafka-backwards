package com.backwards.streaming.kafka

import java.util.concurrent.TimeUnit.SECONDS
import scala.concurrent.duration._
import scala.language.postfixOps
import org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import com.backwards.collection.JavaCollectionOps._
import com.backwards.collection.MapOps._
import com.backwards.streaming.Config._
import com.backwards.streaming.kafka.Geo._
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
    admin createTopics Seq(new NewTopic(topic, partitionCount, replicationFactor.toShort))

    kafkaProps
  }
}

abstract class ConsumerDemoChoosePartition extends DemoChoosePartition {
  val consumerProps = kafkaProps + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[Nothing, String](consumerProps)
  sys addShutdownHook consumer.close
  consumer subscribe Seq(topic)

  while (true) {
    (consumer poll 10.seconds).iterator.foreach(println)
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

    val record = geo(ip) match {
      case Left(e) =>
        new ProducerRecord[String, String](s"$topic-error", ip, s"$e")

      case Right(g: Geo) if g.Country.IsoCode == "US" =>
        new ProducerRecord[String, String](topic, 0, ip, g.toString)

      case Right(g: Geo) =>
        new ProducerRecord[String, String](topic, g.Country.IsoCode.hashCode % partitionCount + 1, ip, g.toString)
    }

    println(s"Producing: $record")
    producer send record
    SECONDS sleep 10
  }
}