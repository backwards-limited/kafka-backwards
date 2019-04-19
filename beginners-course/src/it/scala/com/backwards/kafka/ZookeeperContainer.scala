package com.backwards.kafka

import org.testcontainers.containers.wait.strategy.Wait
import com.backwards.container.GenericContainer

class ZookeeperContainer extends GenericContainer("confluentinc/cp-zookeeper:latest") {
  val port = 2181

  override def configure(): Unit = {
    addFixedExposedPort(port, port)

    withEnv("ZOOKEEPER_CLIENT_PORT", s"$port")

    waitingFor(Wait.defaultWaitStrategy)
  }
}

object ZookeeperContainer {
  def apply(): ZookeeperContainer = new ZookeeperContainer
}