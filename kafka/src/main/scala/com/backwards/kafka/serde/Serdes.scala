package com.backwards.kafka.serde

import java.util
import shapeless._
import org.apache.kafka.common.serialization.{Deserializer, Serializer, Serde => ApacheSerde}

object Serdes extends Serdes

trait Serdes extends Serialize with Deserialize {
  implicit def hnilSerde: ApacheSerde[HNil] = new ApacheSerde[HNil] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    override def close(): Unit = ()

    def serializer(): Serializer[HNil] = hnilSerializer

    def deserializer(): Deserializer[HNil] = hnilDeserializer
  }

  implicit def hlistSerde[H, T <: HList](implicit hSerializer: Serializer[H], hDeserializer: Deserializer[H], tSerializer: Serializer[T], tDeserializer: Deserializer[T]): ApacheSerde[H :: T] = new ApacheSerde[H :: T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    override def close(): Unit = ()

    def serializer(): Serializer[H :: T] = hlistSerializer[H, T]

    def deserializer(): Deserializer[H :: T] = hlistDeserializer[H, T]
  }

  implicit def genericSerde[A, R <: HList](implicit gen: Generic.Aux[A, R], rSerializer: Serializer[R], rDeserializer: Deserializer[R]): ApacheSerde[A] = new ApacheSerde[A] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    override def close(): Unit = ()

    def serializer(): Serializer[A] = genericSerializer[A, R]

    def deserializer(): Deserializer[A] = genericDeserializer[A, R]
  }

  def apply[A: ApacheSerde]: ApacheSerde[A] = implicitly[ApacheSerde[A]]

  def serializer[A: ApacheSerde]: Serializer[A] = apply[A].serializer

  def serialize[A: ApacheSerde](topic: String, a: A): Array[Byte] = serializer[A].serialize(topic, a)

  def deserializer[A: ApacheSerde]: Deserializer[A] = apply[A].deserializer

  def deserialize[A: ApacheSerde](topic: String, bytes: Array[Byte]): A = deserializer[A].deserialize(topic, bytes)
}