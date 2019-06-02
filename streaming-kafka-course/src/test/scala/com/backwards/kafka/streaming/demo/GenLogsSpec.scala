package com.backwards.kafka.streaming.demo

import org.scalatest.{MustMatchers, WordSpec}

class GenLogsSpec extends WordSpec with MustMatchers {
  "Gen logs" should {
    "generate" in {
      GenLogs.generate()
    }
  }
}