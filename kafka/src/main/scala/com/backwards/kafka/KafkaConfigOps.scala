package com.backwards.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig.{KEY_DESERIALIZER_CLASS_CONFIG, VALUE_DESERIALIZER_CLASS_CONFIG}
import org.apache.kafka.clients.producer.ProducerConfig.{KEY_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG}
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

trait KafkaConfigOps {
  def keySerializerProperty[K: Serializer]: (String, String) = KEY_SERIALIZER_CLASS_CONFIG -> implicitly[Serializer[K]].getClass.getName

  def valueSerializerProperty[V: Serializer]: (String, String) = VALUE_SERIALIZER_CLASS_CONFIG -> implicitly[Serializer[V]].getClass.getName

  def keyDeserializerProperty[K: Deserializer]: (String, String) = KEY_DESERIALIZER_CLASS_CONFIG -> implicitly[Deserializer[K]].getClass.getName

  def valueDeserializerProperty[V: Deserializer]: (String, String) = VALUE_DESERIALIZER_CLASS_CONFIG -> implicitly[Deserializer[V]].getClass.getName
}
