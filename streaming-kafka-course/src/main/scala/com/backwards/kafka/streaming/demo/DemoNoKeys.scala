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
import com.backwards.text.StringOps._
import com.backwards.time.DurationOps._
import com.typesafe.scalalogging.LazyLogging

trait DemoNoKeys extends App with LazyLogging {
  val topic: String = "demo-no-keys"

  val clientId: String =
    lowerKebab(getClass.getSimpleName.replaceAll("\\$", ""))

  val kafkaProps = {
    val kafkaProps: Map[String, String] =
      load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> clientId)

    val admin: AdminClient = AdminClient create kafkaProps
    admin createTopics asJavaCollection(Seq(new NewTopic(topic, 4, 1)))

    kafkaProps
  }
}

abstract class ConsumerDemoNoKeys extends DemoNoKeys {
  val consumerProps = kafkaProps + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[Nothing, String](consumerProps)
  sys addShutdownHook consumer.close
  consumer subscribe asJavaCollection(Seq(topic))

  while (true) {
    (consumer poll 10.seconds).iterator.asScala.foreach(println)
  }
}

object ConsumerDemoNoKeys1 extends ConsumerDemoNoKeys

object ConsumerDemoNoKeys2 extends ConsumerDemoNoKeys

object ProducerDemoNoKeys extends DemoNoKeys {
  val producer = new KafkaProducer[Nothing, String](kafkaProps)
  sys addShutdownHook producer.close

  while (true) {
    val record = new ProducerRecord[Nothing, String](topic, randomString(10))
    println(s"Producing: $record")
    producer send record
    SECONDS sleep 10
  }
}