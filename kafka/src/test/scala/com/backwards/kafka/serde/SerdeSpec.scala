package com.backwards.kafka.serde

import shapeless.Generic
import org.scalatest.{MustMatchers, WordSpec}

class SerdeSpec extends WordSpec with MustMatchers with Serde.Implicits {
  "" should {
    "" in {

      println(apply[Foo].serialize("topic", Foo()))
    }
  }
}

case class Foo()