package com.backwards.kafka.serde

import java.util
import java.util.Date
import shapeless._
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer}

trait Deserialize {
  implicit val stringDeserializer: Deserializer[String] = new StringDeserializer

  implicit val booleanDeserializer: Deserializer[Boolean] = new BooleanDeserializer

  implicit val intDeserializer: Deserializer[Int] = new IntDeserializer

  // implicit val longDeserializer: Deserializer[Long] = new LongDeserializer TODO

  // implicit val doubleDeserializer: Deserializer[Double] = new DoubleDeserializer TODO

  // implicit val floatDeserializer: Deserializer[Float] = new FloatDeserializer TODO

  implicit val dateDeserializer: Deserializer[Date] = new DateDeserializer

  implicit def genericDeserializer[A, R <: HList](implicit gen: Generic.Aux[A, R], rDeserializer: Deserializer[R]): Deserializer[A] = new Deserializer[A] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): A = gen.from(rDeserializer.deserialize(topic, data))

    def close(): Unit = ()
  }

  implicit def hnilDeserializer: Deserializer[HNil] = new Deserializer[HNil] {
    def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): HNil = HNil

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

  def apply[A: Deserializer]: Deserializer[A] = implicitly[Deserializer[A]]

  def deserialize[A: Deserializer](topic: String, data: Array[Byte]): A = apply[A].deserialize(topic, data)
}