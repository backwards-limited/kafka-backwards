package com.backwards.kafka

import java.util.concurrent.TimeUnit
import io.lemonlabs.uri.Uri
import org.testcontainers.containers.wait.strategy.Wait
import com.backwards.container.GenericContainer

// TODO - WIP - Do not use as there is an underlying issue with "test containers" - Instead use DockerComposeFixture / DockerCompose
class KafkaContainer(zookeeperContainer: ZookeeperContainer) extends GenericContainer("confluentinc/cp-kafka:latest") {
  val port = 9092

  lazy val uri: Uri = Uri.parse(s"http://$getContainerIpAddress:$port")

  override def configure(): Unit = {
    addFixedExposedPort(port, port)

    withEnv("KAFKA_ZOOKEEPER_CONNECT", s"$networkName:${zookeeperContainer.port}")
    withEnv("KAFKA_ADVERTISED_LISTENERS", s"PLAINTEXT://127.0.0.1:$port")
    withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
    withEnv("KAFKA_BROKER_ID", "1")
    withExposedPorts(port)

    waitingFor(Wait.defaultWaitStrategy)
  }
}

object KafkaContainer {
  def apply(): (ZookeeperContainer, KafkaContainer) = {
    val zookeeperContainer = ZookeeperContainer()
    TimeUnit.SECONDS.sleep(3)

    val kafkaContainer = apply(zookeeperContainer)

    (zookeeperContainer, kafkaContainer)
  }

  def apply(zookeeperContainer: ZookeeperContainer): KafkaContainer = {
    val kc = new KafkaContainer(zookeeperContainer)
    TimeUnit.SECONDS.sleep(3)

    kc
  }
}