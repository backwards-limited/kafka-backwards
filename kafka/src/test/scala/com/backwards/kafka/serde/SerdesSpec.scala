package com.backwards.kafka.serde

import java.util.Date
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

// TODO - Scalacheck
class SerdesSpec extends AnyWordSpec with Matchers with Serdes {
  spec =>

  final case class Foo(data: String, more: String)

  final case class Bar(length: Int, date: Date, high: Boolean, low: Boolean)

  val topic = "topic"
  val foo: Foo = Foo("scooby", "doo")

  "Serde" should {
    "serialize and deserialize via object application" in {
      val bytes = Serdes[Foo].serializer.serialize(topic, spec.foo)

      val foo = Serdes[Foo].deserializer.deserialize(topic, bytes)

      foo mustEqual spec.foo
    }

    "serialize and deserialize via function application" in {
      val bytes = serializer[Foo].serialize(topic, spec.foo)

      val foo = deserializer[Foo].deserialize(topic, bytes)

      foo mustEqual spec.foo
    }

    "serialize and deserialize via direct functions" in {
      val bytes = serialize[Foo](topic, spec.foo)

      val foo = deserialize[Foo](topic, bytes)

      foo mustEqual spec.foo
    }

    // TODO - Scalacheck
    "serialize and deserialize something else" in {
      val bar = Bar(length = 66, new Date, high = true, low = false)
      val bytes = serialize[Bar](topic, bar)

      deserialize[Bar](topic, bytes) mustEqual bar
    }
  }
}