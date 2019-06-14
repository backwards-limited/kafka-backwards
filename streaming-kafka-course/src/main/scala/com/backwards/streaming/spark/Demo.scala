package com.backwards.streaming.spark

import com.typesafe.scalalogging.LazyLogging
import com.backwards.streaming.Config._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Demo extends App with LazyLogging {
  val sparkConfig: SparkConfig = load[SparkConfig]("spark")

  val conf: SparkConf =
    new SparkConf()
      .setAppName("Get streaming department traffic")
      .setMaster(sparkConfig.execution.mode)

  val ssc: StreamingContext =
    new StreamingContext(conf, Seconds(30))

  val lines: ReceiverInputDStream[String] =
    ssc.socketTextStream(sparkConfig.data.host, sparkConfig.data.port)

  val departmentTraffic = {
    val api: String => Array[String] = _.split(" ")(5).split("/")

    lines
      .filter(line => api(line)(1) == "department")
      .map(line => (api(line)(2), 1))
      .reduceByKey(_ + _)
  }

  departmentTraffic saveAsTextFiles sparkConfig.output.dir.toString

  ssc.start()
  ssc.awaitTermination()
}