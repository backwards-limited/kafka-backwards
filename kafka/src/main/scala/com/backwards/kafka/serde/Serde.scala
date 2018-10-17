package com.backwards.kafka.serde

import org.apache.kafka.common.serialization._

object Serde {
  object Implicits extends Implicits

  trait Implicits {
    // TODO - Shapeless automatic derivation
    implicit val stringSerializer: Serializer[String] = new StringSerializer
    implicit val stringDeserializer: Deserializer[String] = new StringDeserializer
  }
}