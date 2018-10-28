package com.backwards.kafka.config

trait KafkaConfigValue[A] {
  def asValue(a: A): String
}

// TODO - Shapeless
object KafkaConfigValue {
  def apply[A: KafkaConfigValue]: KafkaConfigValue[A] = implicitly[KafkaConfigValue[A]]

  implicit object StringKafkaConfigValue extends KafkaConfigValue[String] {
    def asValue(a: String): String = a
  }

  implicit object IntKafkaConfigValue extends KafkaConfigValue[Int] {
    def asValue(a: Int): String = a.toString
  }

  implicit object DoubleKafkaConfigValue extends KafkaConfigValue[Double] {
    def asValue(a: Double): String = a.toString
  }

  implicit object BooleanKafkaConfigValue extends KafkaConfigValue[Boolean] {
    def asValue(a: Boolean): String = a.toString
  }
}