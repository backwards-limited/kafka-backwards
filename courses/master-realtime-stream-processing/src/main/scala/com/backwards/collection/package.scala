package com.backwards

import java.util.Properties
import scala.jdk.CollectionConverters.MapHasAsJava

package object collection {
  def toProperties(m: Map[String, String]): Properties = {
    val props = new Properties
    props.putAll(m.asJava)
    props
  }
}