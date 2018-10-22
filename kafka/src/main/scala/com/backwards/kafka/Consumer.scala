package com.backwards.kafka

import java.time.{Duration => JDuration}
import scala.collection.JavaConverters._
import scala.concurrent.duration.{Duration, _}
import scala.language.{higherKinds, postfixOps}
import cats.Applicative
import cats.implicits._
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer

object Consumer extends ConfigurationOps {
  def apply[F[_]: Applicative, K, V](configuration: Configuration)(implicit K: Deserializer[K], V: Deserializer[V]) =
    new Consumer[F, K, V](configuration + keyDeserializerProperty[K] + valueDeserializerProperty[V])
}

class Consumer[F[_]: Applicative, K, V] private (configuration: Configuration) {
  lazy val consumer: KafkaConsumer[K, V] = {
    val consumer = new KafkaConsumer[K, V](configuration)
    consumer.subscribe(Seq(configuration.topic).asJava)
    consumer
  }

  def poll(duration: Duration = 10 seconds): F[Seq[(K, V)]] = {
    val consumerRecords = consumer.poll(JDuration.ofMillis(duration.toMillis))

    consumerRecords.iterator().asScala.map { consumerRecord =>
      (consumerRecord.key, consumerRecord.value)
    }.toSeq.pure[F]
  }
}