package com.backwards.kafka.github

import scala.util.Try

object Version {
  def apply(): String =
    Try(getClass.getPackage.getImplementationVersion) getOrElse "0.0.0.0"
}