package com.backwards.kafka.serde

import java.util
import java.util.Date
import shapeless._
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import com.backwards.kafka.serde.BooleanSerde.BooleanSerializer
import com.backwards.kafka.serde.DateSerde.DateSerializer
import com.backwards.kafka.serde.IntSerde.IntSerializer

trait Serialize {
  implicit val stringSerializer: Serializer[String] = new StringSerializer

  implicit val booleanSerializer: Serializer[Boolean] = new BooleanSerializer

  implicit val intSerializer: Serializer[Int] = new IntSerializer

  // implicit val longSerializer: Serializer[Long] = new LongSerializer TODO

  // implicit val doubleSerializer: Serializer[Double] = new DoubleSerializer TODO

  // implicit val floatSerializer: Serializer[Float] = new FloatSerializer TODO

  implicit val dateSerializer: Serializer[Date] = new DateSerializer

  implicit def genericSerializer[A, R <: HList](implicit gen: Generic.Aux[A, R], rSerializer: Serializer[R]): Serializer[A] = new Serializer[A] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: A): Array[Byte] = rSerializer.serialize(topic, gen.to(data))

    override def close(): Unit = ()
  }

  implicit def hnilSerializer: Serializer[HNil] = new Serializer[HNil] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: HNil): Array[Byte] = Array.emptyByteArray

    override def close(): Unit = ()
  }

  implicit def hlistSerializer[H, T <: HList](implicit hSerializer: Serializer[H], tSerializer: Serializer[T]): Serializer[H :: T] = new Serializer[H :: T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    // TODO - WIP - 1st iteration is a hack by using the simplest delimiter
    def serialize(topic: String, data: H :: T): Array[Byte] = data match {
      case h :: t => hSerializer.serialize(topic, h) ++ "|".getBytes ++ tSerializer.serialize(topic, t)
    }

    override def close(): Unit = ()
  }

  def apply[A: Serializer]: Serializer[A] = implicitly[Serializer[A]]

  def serialize[A: Serializer](topic: String, a: A): Array[Byte] = apply[A].serialize(topic, a)
}