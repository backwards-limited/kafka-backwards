package com.backwards.kafka.admin

import java.util.Properties
import java.util.concurrent.TimeUnit._
import scala.annotation.tailrec
import scala.jdk.CollectionConverters._
import scala.util.Try
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, DeleteTopicsResult, NewTopic}
import com.backwards.JavaOps
import com.backwards.kafka.admin.KafkaAdmin._

trait KafkaAdmin extends JavaOps {
  def newAdminClient(props: Map[String, String]): AdminClient =
    AdminClient create props

  @deprecated(message = "User equivalent function that takes Map", since = "1st September 2019")
  def newAdminClient(properties: Properties = default): AdminClient =
    AdminClient create properties

  def newTopic(name: String, numberOfPartitions: Int, replicationFactor: Int, configs: (String, String)*): NewTopic =
    new NewTopic(name, numberOfPartitions, replicationFactor.toShort).configs(configs.toMap[String, String])

  def createTopic(name: String, numberOfPartitions: Int, replicationFactor: Int, configs: (String, String)*)(implicit adminClient: AdminClient): NewTopic = {
    delete(name)

    Try {
      val topic: NewTopic = newTopic(name, numberOfPartitions, replicationFactor, configs: _*)
      adminClient createTopics topic
      scribe info s"Topic ${topic.name} configuration: ${adminClient.describeTopics(topic.name).all.get.asScala.mkString(", ")}"
      topic
    } getOrElse {
      MILLISECONDS sleep 500
      createTopic(name, numberOfPartitions, replicationFactor)
    }
  }

  @tailrec
  final def delete(topic: String)(implicit adminClient: AdminClient): DeleteTopicsResult = {
    val result: DeleteTopicsResult = adminClient deleteTopics topic

    if (adminClient.listTopics().names().get().asScala.contains(topic)) {
      SECONDS sleep 1
      delete(topic)
    } else {
      result
    }
  }
}

object KafkaAdmin {
  @deprecated(message = "Just don't like this", since = "1st September 2019")
  lazy val default: Properties = {
    val properties = new Properties()
    properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties
  }
}