package com.backwards.kafka

import java.util.Properties
import org.scalatest.{MustMatchers, WordSpec}

class ConfigurationSpec extends WordSpec with MustMatchers {
  "Configuration" should {
    val topic = "test-topic"
    val keyValue1 = "key-1" -> "value-1"
    val keyValue2 = "key-2" -> "value-2"
    val configuration = Configuration(topic)

    "be instantiated with all requirements" in {
      configuration must have (
        'topic (topic),
        'props (Map.empty[String, String])
      )
    }

    "have properties added to it" in {
      val configurationWithProps = configuration + keyValue1 + keyValue2

      configurationWithProps must have (
        'topic (topic),
        'props (Map(keyValue1, keyValue2))
      )
    }

    "be converted to (Java) Properties" in {
      configuration.toProperties mustEqual new Properties
    }

    "be converted to (Java) Properties which include properties that were already added" in {
      val properties = new Properties
      properties.setProperty(keyValue1._1, keyValue1._2)
      properties.setProperty(keyValue2._1, keyValue2._2)

      val configurationWithProps = configuration + keyValue1 + keyValue2

      configurationWithProps.toProperties mustEqual properties
    }
  }
}
