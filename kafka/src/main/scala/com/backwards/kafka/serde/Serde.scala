package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.serialization._
import scala.collection.JavaConverters._

object Serde {
  object Implicits extends Implicits

  trait Implicits {
    // TODO - Shapeless automatic derivation
    implicit val stringSerializer: Serializer[String] = new StringSerializer
    implicit val stringDeserializer: Deserializer[String] = new StringDeserializer

    import shapeless._

    implicit def hnilSerializer = new Serializer[HNil] {
      def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ???

      def serialize(topic: String, data: HNil): Array[Byte] = Array.emptyByteArray

      def close(): Unit = ???
    }

    implicit def hlistSerializer[H, T <: HList](implicit hSerializer: Serializer[H], tSerializer: Serializer[T]) = new Serializer[H :: T] {
      def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ???

      def serialize(topic: String, data: H :: T): Array[Byte] = data match {
        case h :: t => hSerializer.serialize(topic, h) ++ tSerializer.serialize(topic, t)
      }

      def close(): Unit = ???
    }

    implicit def genericSerializer[A, R <: HList](implicit gen: Generic.Aux[A, R], serializer: Serializer[R]) = new Serializer[A] {
      def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ???

      def serialize(topic: String, data: A): Array[Byte] = serializer.serialize(topic, gen.to(data))

      def close(): Unit = ???
    }

    def apply[A: Serializer] = implicitly[Serializer[A]]
  }
}