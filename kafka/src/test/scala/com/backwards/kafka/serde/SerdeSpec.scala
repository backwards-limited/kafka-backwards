package com.backwards.kafka.serde

import org.scalatest.{MustMatchers, WordSpec}

class SerdeSpec extends WordSpec with MustMatchers with Serde {
  spec =>

  case class Foo(data: String)

  val topic = "topic"
  val foo = Foo("scooby")

  "Serde" should {
    "serialize and deserialize via object application" in {
      val bytes = Serde[Foo].serializer.serialize(topic, spec.foo)

      val foo = Serde[Foo].deserializer.deserialize(topic, bytes)

      foo mustEqual spec.foo
    }

    "serialize and deserialize via function application" in {
      val bytes = serializer[Foo].serialize(topic, spec.foo)

      val foo = deserializer[Foo].deserialize(topic, bytes)

      foo mustEqual spec.foo
    }

    "serialize and deserialize via direct functions" in {
      val bytes = serialize(topic, spec.foo)

      val foo = deserialize[Foo](topic, bytes)

      foo mustEqual spec.foo
    }
  }
}