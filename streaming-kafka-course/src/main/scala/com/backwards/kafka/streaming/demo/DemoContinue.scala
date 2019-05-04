package com.backwards.kafka.streaming.demo

import java.util.concurrent.TimeUnit.MILLISECONDS
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random
import com.backwards.collection.MapOps._
import com.backwards.io.Continue
import com.backwards.kafka.streaming.Config._
import com.backwards.time.DurationOps._

object ConsumerDemoWithContinue extends Demo with Continue {
  import org.apache.kafka.clients.consumer.ConsumerConfig.{CLIENT_ID_CONFIG, GROUP_ID_CONFIG}
  import org.apache.kafka.clients.consumer.KafkaConsumer

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> "consumer-demo") + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[String, String](kafkaProps)
  consumer subscribe asJavaCollection(Seq(topic))

  def consume(): Unit = {
    (consumer poll 10.seconds).iterator.asScala.foreach(println)

    if (continue.get) consume()
  }

  checkContinue()
  consume()
  consumer.close()
}

object ProducerDemoWithContinue extends Demo with Continue {
  import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
  import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

  val kafkaProps = load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> "producer-demo")

  val producer = new KafkaProducer[String, String](kafkaProps)

  def randomString(length: Int) = Stream.continually(Random.nextPrintableChar) take length mkString

  def produce(sleep: Duration = 10 seconds): Unit = {
    val record = new ProducerRecord[String, String](topic, randomString(2), randomString(10))
    println(s"Producing: $record")
    producer send record
    MILLISECONDS sleep sleep.toMillis

    if (continue.get) produce(sleep)
  }

  checkContinue()
  produce()
  producer.close()
}