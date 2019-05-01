package com.backwards.kafka.streaming

import java.util.Properties
import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG

final case class KafkaConfig(bootstrap: Bootstrap, props: Map[String, String] = Map.empty[String, String]) {
  def toProperties: Properties =
    (new Properties /: (props + (BOOTSTRAP_SERVERS_CONFIG -> bootstrap.servers.replaceAll("\\s", "")))) {
      case (ps, (k, v)) =>
        ps.put(k, v)
        ps
    }
}

final case class Bootstrap(servers: String)