package com.backwards

import java.io.FileInputStream
import java.util.Properties
import scala.collection.JavaConverters._
import com.backwards.logging.Logging

trait BackwardsApp extends App with Logging {
  Option(System getProperty "environment") foreach { env =>
    info("Loading given environment into JVM's system properties...")

    val properties = new Properties
    properties load new FileInputStream(env)

    propertiesAsScalaMap(properties) foreach { case (key, value) =>
      info(s"Setting system property: $key...")
      System.setProperty(key, value)
    }
  }
}