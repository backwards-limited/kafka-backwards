package com.backwards.kafka

import java.util.Properties
import io.lemonlabs.uri.Uri
import org.apache.kafka.clients.CommonClientConfigs._
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.{BootstrapConfig, Config}

class KafkaConfigSpec extends WordSpec with MustMatchers {
  "Kafka Config" should {
    val bootstrapServers = "http://www.backwards.page"
    val keyValue1 = "key-1" -> "value-1"
    val keyValue2 = "key-2" -> "value-2"
    val config = KafkaConfig(BootstrapConfig(Seq(Uri parse bootstrapServers)))

    "be instantiated with all requirements" in {
      config must have (
        'properties (Map.empty[String, String])
      )
    }

    "have properties added to it" in {
      val configWithProperties = config + keyValue1 + keyValue2

      configWithProperties must have (
        'properties (Map(keyValue1, keyValue2))
      )
    }

    "be converted to (Java) Properties" in {
      val properties = new Properties
      properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

      config.toProperties mustEqual properties
    }

    "be converted to (Java) Properties which include properties that were already added" in {
      val properties = new Properties
      properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
      properties.setProperty(keyValue1._1, keyValue1._2)
      properties.setProperty(keyValue2._1, keyValue2._2)

      val configWithProperties = config + keyValue1 + keyValue2

      configWithProperties.toProperties mustEqual properties
    }

    "read properties from resource configuration" in new Config {
      val config: KafkaConfig = load[KafkaConfig]("kafka")

      config.properties("acks") mustEqual "all"
      config.properties("min.insync.replicas") mustEqual "2"
      config.properties("enable.idempotence") mustEqual "true"
    }
  }
}