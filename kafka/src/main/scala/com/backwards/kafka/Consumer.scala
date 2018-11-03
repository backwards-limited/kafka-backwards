package com.backwards.kafka

import java.time.{Duration => JDuration}
import scala.collection.JavaConverters._
import scala.concurrent.duration.{Duration, _}
import scala.language.{higherKinds, postfixOps}
import cats.Applicative
import cats.implicits._
import org.apache.kafka.clients.consumer.{Consumer => KafkaConsumer, KafkaConsumer => KafkaConsumerImpl}
import org.apache.kafka.common.serialization.Deserializer
import com.backwards.kafka.config.{KafkaConfig, KafkaConfigOps}

object Consumer extends KafkaConfigOps {
  def apply[F[_]: Applicative, K, V](topic: String, config: KafkaConfig)(implicit K: Deserializer[K], V: Deserializer[V]): Consumer[F, K, V] = {
    lazy val consumer: KafkaConsumer[K, V] = {
      val consumer = new KafkaConsumerImpl[K, V](config + keyDeserializerProperty[K] + valueDeserializerProperty[V])
      consumer.subscribe(Seq(topic).asJava)
      sys addShutdownHook consumer.close()
      consumer
    }

    new Consumer[F, K, V](topic, consumer)
  }
}

class Consumer[F[_]: Applicative, K, V](topic: String, consumer: => KafkaConsumer[K, V]) {
  def poll(duration: Duration = 10 seconds): F[Seq[(K, V)]] = {
    val consumerRecords = consumer.poll(JDuration.ofMillis(duration.toMillis))

    consumerRecords.iterator().asScala.map { consumerRecord =>
      (consumerRecord.key, consumerRecord.value)
    }.toSeq.pure[F]
  }
}