package com.backwards.kafka

import java.util.Properties
import scala.language.implicitConversions
import monocle.macros.syntax.lens._
import org.apache.kafka.clients.consumer.ConsumerConfig.{KEY_DESERIALIZER_CLASS_CONFIG, VALUE_DESERIALIZER_CLASS_CONFIG}
import org.apache.kafka.clients.producer.ProducerConfig.{KEY_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG}
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import com.backwards.logging.Logging

object Configuration {
  implicit def toProperties(c: Configuration): Properties = c.toProperties

  def apply(topic: String): Configuration = new Configuration(topic)
}

case class Configuration(topic: String, props: Map[String, String] = Map.empty[String, String]) extends Logging {
  info(this)

  lazy val toProperties: Properties =
    (new Properties /: props) { case (p, (k, v)) =>
      p.put(k, v)
      p
    }

  def + (kv: (String, String)): Configuration =
    this.lens(_.props).modify(_ + kv)

  def + (key: String, value: String): Configuration =
    this + (key -> value)

  def add(key: String, value: String): Configuration =
    this + (key -> value)
}

trait ConfigurationOps {
  def keySerializerProperty[K: Serializer]: (String, String) = KEY_SERIALIZER_CLASS_CONFIG -> implicitly[Serializer[K]].getClass.getName

  def valueSerializerProperty[V: Serializer]: (String, String) = VALUE_SERIALIZER_CLASS_CONFIG -> implicitly[Serializer[V]].getClass.getName

  def keyDeserializerProperty[K: Deserializer]: (String, String) = KEY_DESERIALIZER_CLASS_CONFIG -> implicitly[Deserializer[K]].getClass.getName

  def valueDeserializerProperty[V: Deserializer]: (String, String) = VALUE_DESERIALIZER_CLASS_CONFIG -> implicitly[Deserializer[V]].getClass.getName
}