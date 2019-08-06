package com.backwards

import java.util
import scala.collection.JavaConverters._
import scala.language.implicitConversions

object JavaOps extends JavaOps

trait JavaOps {
  implicit def toJava[T](t: T): util.Collection[T] = toJava(Seq(t))

  implicit def toJava[T](ts: Seq[T]): util.Collection[T] = ts.asJavaCollection

  implicit def toJava[K, V](m: Map[K, V]): util.Map[K, V] = m.asJava
}