package com.backwards.streaming.kafka

import org.scalatest.{MustMatchers, WordSpec}

class GenLogsSpec extends WordSpec with MustMatchers {
  "Gen logs" should {
    "generate" in {
      GenLogs.generate.take(5).compile.toList.unsafeRunSync.foreach(println)
    }
  }
}