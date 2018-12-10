package com.backwards.kafka

import java.net.URI
import org.testcontainers.containers.wait.strategy.Wait
import com.backwards.container.GenericContainer

class KafkaContainer(zookeeperContainer: ZookeeperContainer) extends GenericContainer("confluentinc/cp-kafka:latest") {
  val port = 9092

  lazy val uri = new URI(s"http://$getContainerIpAddress:$port")

  override def configure(): Unit = {
    addFixedExposedPort(port, port)

    withEnv("KAFKA_ZOOKEEPER_CONNECT", s"$networkName:${zookeeperContainer.port}")
    withEnv("KAFKA_ADVERTISED_LISTENERS", s"PLAINTEXT://127.0.0.1:$port")
    withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
    withEnv("KAFKA_BROKER_ID", "1")

    waitingFor(Wait.defaultWaitStrategy)
  }
}

object KafkaContainer {
  def apply(zookeeperContainer: ZookeeperContainer) = new KafkaContainer(zookeeperContainer)
}