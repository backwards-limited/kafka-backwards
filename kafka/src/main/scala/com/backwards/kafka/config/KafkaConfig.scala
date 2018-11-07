package com.backwards.kafka.config

import java.util.Properties
import scala.language.implicitConversions
import monocle.macros.syntax.lens._
import org.apache.kafka.clients.CommonClientConfigs._
import com.backwards.config.BootstrapConfig

object KafkaConfig {
  implicit def toProperties(c: KafkaConfig): Properties = c.toProperties

  def apply(bootstrap: BootstrapConfig): KafkaConfig =
    KafkaConfig(bootstrap, Map.empty[String, String])

  trait ToString[A] {
    def apply(a: A): String
  }

  // TODO - Shapeless
  object ToString {
    def apply[A: ToString](a: A): String = implicitly[ToString[A]].apply(a)

    implicit object StringToString extends ToString[String] {
      def apply(a: String): String = a
    }

    implicit object IntToString extends ToString[Int] {
      def apply(a: Int): String = a.toString
    }

    implicit object DoubleToString extends ToString[Double] {
      def apply(a: Double): String = a.toString
    }

    implicit object BooleanToString extends ToString[Boolean] {
      def apply(a: Boolean): String = a.toString
    }
  }
}

/**
  *
  * @param bootstrap BootstrapConfig
  * @param properties Map[String, String]
  * Included the Java friendly operation [[add]].
  */
final case class KafkaConfig(bootstrap: BootstrapConfig, properties: Map[String, String] = Map.empty[String, String]) {
  import KafkaConfig._

  lazy val toProperties: Properties =
    (new Properties /: (properties + (BOOTSTRAP_SERVERS_CONFIG -> bootstrap.bootstrapServers))) { case (p, (k, v)) =>
      p.put(k, v)
      p
    }

  def +[A: ToString](key: String, value: A): KafkaConfig =
    this.lens(_.properties).modify(_ + (key -> ToString[A](value)))

  def +[A: ToString](kv: (String, A)): KafkaConfig =
    this + (kv._1, kv._2)

  def add(key: String, value: String): KafkaConfig =
    this + (key, value)
}