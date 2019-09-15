package com.backwards.kafka.streams

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{LongDeserializer, StringDeserializer, StringSerializer}
import org.apache.kafka.streams.test.{ConsumerRecordFactory, OutputVerifier}
import org.apache.kafka.streams.{StreamsConfig, TopologyTestDriver}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{Assertion, MustMatchers}
import com.backwards.collection.MapOps._
import com.backwards.kafka.streams.WordCountTestableApp._

class WordCountTestableAppSpec extends AnyWordSpec with MustMatchers {
  def withTopologyTestDriver(test: TopologyTestDriver => Assertion) {
    val props: Map[String, String] = Map(
      StreamsConfig.APPLICATION_ID_CONFIG -> "word-count",
      StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "dummy:1234",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )

    val topologyTestDriver = new TopologyTestDriver(topology, props)

    try test(topologyTestDriver) // "loan" the fixture to the test
    finally topologyTestDriver.close()
  }

  "Word Count Testable App" should {
    "have valid topology" in withTopologyTestDriver { topologyTestDriver =>
      val consumerRecordFactory = new ConsumerRecordFactory[String, String](new StringSerializer, new StringSerializer)

      // First message to test
      topologyTestDriver.pipeInput(consumerRecordFactory.create("word-count-input", null, "TESTING Kafka Streams"))

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "testing", Long.box(1)
      )

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "kafka", Long.box(1)
      )

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "streams", Long.box(1)
      )

      topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer) mustBe null

      // Second message to test
      topologyTestDriver.pipeInput(consumerRecordFactory.create("word-count-input", null, "testing Kafka again"))

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "testing", Long.box(2)
      )

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "kafka", Long.box(2)
      )

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "again", Long.box(1)
      )

      topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer) mustBe null
    }

    "have valid topology again" in withTopologyTestDriver { topologyTestDriver =>
      val consumerRecordFactory = new ConsumerRecordFactory[String, String](new StringSerializer, new StringSerializer)

      topologyTestDriver.pipeInput(consumerRecordFactory.create("word-count-input", null, "kafka Kafka KAFKA"))

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "kafka", Long.box(1)
      )

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "kafka", Long.box(2)
      )

      OutputVerifier.compareKeyValue(
        topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer),
        "kafka", Long.box(3)
      )

      topologyTestDriver.readOutput("word-count-output", new StringDeserializer, new LongDeserializer) mustBe null
    }
  }
}