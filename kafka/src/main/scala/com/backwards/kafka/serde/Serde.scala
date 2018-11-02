package com.backwards.kafka.serde

import java.util
import shapeless._
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer, Serde => ApacheSerde}

object Serde extends Serde

trait Serde {
  implicit val stringSerializer: Serializer[String] = new StringSerializer
  implicit val stringDeserializer: Deserializer[String] = new StringDeserializer

  implicit val intSerializer: Serializer[Int] = new IntSerializer
  implicit val intDeserializer: Deserializer[Int] = new IntDeserializer

  implicit def hnilSerializer: Serializer[HNil] = new Serializer[HNil] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: HNil): Array[Byte] = Array.emptyByteArray

    def close(): Unit = ()
  }

  implicit def hnilDeserializer: Deserializer[HNil] = new Deserializer[HNil] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): HNil = HNil

    def close(): Unit = ()
  }

  implicit def hlistSerializer[H, T <: HList](implicit hSerializer: Serializer[H], tSerializer: Serializer[T]): Serializer[H :: T] = new Serializer[H :: T] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    // TODO - WIP - 1st iteration is a hack by using the simplest delimiter
    def serialize(topic: String, data: H :: T): Array[Byte] = data match {
      case h :: t => hSerializer.serialize(topic, h) ++ "|".getBytes ++ tSerializer.serialize(topic, t)
    }

    def close(): Unit = ()
  }

  implicit def hlistDeserializer[H, T <: HList](implicit hDeserializer: Deserializer[H], tDeserializer: Deserializer[T]): Deserializer[H :: T] = new Deserializer[H :: T] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    // TODO - WIP - 1st iteration is a hack by using the simplest delimiter
    def deserialize(topic: String, data: Array[Byte]): H :: T = {
      val index = data.indexWhere(_.toChar.toString == "|" )
      val (h, t) = data.splitAt(index)

      hDeserializer.deserialize(topic, h) :: tDeserializer.deserialize(topic, t.tail)
    }

    def close(): Unit = ()
  }

  implicit def genericSerializer[A, R <: HList](implicit gen: Generic.Aux[A, R], rSerializer: Serializer[R]): Serializer[A] = new Serializer[A] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: A): Array[Byte] = rSerializer.serialize(topic, gen.to(data))

    def close(): Unit = ()
  }

  implicit def genericDeserializer[A, R <: HList](implicit gen: Generic.Aux[A, R], rDeserializer: Deserializer[R]): Deserializer[A] = new Deserializer[A] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): A = gen.from(rDeserializer.deserialize(topic, data))

    def close(): Unit = ()
  }

  implicit def hnilSerde: ApacheSerde[HNil] = new ApacheSerde[HNil] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def close(): Unit = ()

    def serializer(): Serializer[HNil] = hnilSerializer

    def deserializer(): Deserializer[HNil] = hnilDeserializer
  }

  implicit def hlistSerde[H, T <: HList](implicit hSerializer: Serializer[H], hDeserializer: Deserializer[H], tSerializer: Serializer[T], tDeserializer: Deserializer[T]): ApacheSerde[H :: T] = new ApacheSerde[H :: T] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def close(): Unit = ()

    def serializer(): Serializer[H :: T] = hlistSerializer[H, T]

    def deserializer(): Deserializer[H :: T] = hlistDeserializer[H, T]
  }

  implicit def genericSerde[A, R <: HList](implicit gen: Generic.Aux[A, R], rSerializer: Serializer[R], rDeserializer: Deserializer[R]): ApacheSerde[A] = new ApacheSerde[A] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def close(): Unit = ()

    def serializer(): Serializer[A] = genericSerializer[A, R]

    def deserializer(): Deserializer[A] = genericDeserializer[A, R]
  }

  def apply[A: ApacheSerde]: ApacheSerde[A] = implicitly[ApacheSerde[A]]

  def serializer[A: ApacheSerde]: Serializer[A] = apply[A].serializer

  def serialize[A: ApacheSerde](topic: String, a: A): Array[Byte] = serializer[A].serialize(topic, a)

  def deserializer[A: ApacheSerde]: Deserializer[A] = apply[A].deserializer

  def deserialize[A: ApacheSerde](topic: String, bytes: Array[Byte]): A = deserializer[A].deserialize(topic, bytes)
}