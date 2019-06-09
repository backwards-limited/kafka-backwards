package com.backwards.streaming.spark

import com.typesafe.scalalogging.LazyLogging
import com.backwards.streaming.Config._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext

object Demo extends App with LazyLogging {
  val sparkConfig = load[SparkConfig]("spark")
}