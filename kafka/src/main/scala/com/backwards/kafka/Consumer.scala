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
    lazy val kafkaConsumer: KafkaConsumer[K, V] = {
      val kafkaConsumer = new KafkaConsumerImpl[K, V](config + keyDeserializerProperty[K] + valueDeserializerProperty[V])
      kafkaConsumer.subscribe(Seq(topic).asJava)
      sys addShutdownHook kafkaConsumer.close()
      kafkaConsumer
    }

    new Consumer[F, K, V](topic, kafkaConsumer)
  }
}

class Consumer[F[_]: Applicative, K, V](topic: String, kafkaConsumer: => KafkaConsumer[K, V]) {
  lazy val underlying: KafkaConsumer[K, V] = kafkaConsumer

  def poll(duration: Duration = 10 seconds): F[Seq[(K, V)]] = {
    val records = kafkaConsumer poll JDuration.ofMillis(duration.toMillis)

    records.iterator.asScala.map { consumerRecord =>
      // TODO - Code that calls "poll" usually needs to be "idempotent" and to help we could generate a unique key that makes sure duplicate messages are handled the same.
      // TODO - Instead of providing "consumerRecored.key", we could provide: val id = s"${record.topic}-${record.partition}-${record.offset}"
      (consumerRecord.key, consumerRecord.value)
    }.toSeq.pure[F]
  }
}