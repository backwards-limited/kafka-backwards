package com.backwards.streaming

import scala.language.experimental.macros
import scala.reflect.ClassTag
import pureconfig.{ConfigReader, Derivation}
import com.typesafe.scalalogging.LazyLogging

object Config extends Config

trait Config extends com.backwards.config.Config with LazyLogging {
  val env: String = sys.env.getOrElse("ENV", "dev")

  val configClasspath: String = s"application.$env.conf"

  override def load[C](namespace: String)(implicit evidence$1: ClassTag[C], READER: Derivation[ConfigReader[C]]): C = {
    val config: C = load(configClasspath, namespace)
    logger info config.toString
    config
  }
}