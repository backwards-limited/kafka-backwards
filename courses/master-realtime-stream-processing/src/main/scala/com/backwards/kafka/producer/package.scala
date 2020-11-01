package com.backwards.kafka

import java.util.Properties
import cats.effect.IO
import io.chrisdavenport.log4cats.Logger
import pureconfig.ConfigSource
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import com.backwards.collection.toProperties

package object producer {
  lazy val defaultProducerProperties: Properties = {
    val props = new Properties
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer")
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094")

    // TODO - Remove the following
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[IntegerSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    props
  }

  def producerProperties(implicit Logger: Logger[IO]): IO[Properties] =
    IO(ConfigSource.default.at("producer").load[Map[String, String]]) flatMap {
      case Right(producerConfig) =>
        IO(toProperties(producerConfig))

      case Left(configReaderFailures) =>
        Logger.warn(s"Defaulting producer config since failed to load producer config: $configReaderFailures") *> IO(defaultProducerProperties)
    }
}