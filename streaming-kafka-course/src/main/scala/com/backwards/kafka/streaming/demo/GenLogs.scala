package com.backwards.kafka.streaming.demo

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.Random
import better.files.File
import cats.effect.IO
import cats.implicits._
import fs2._
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object GenLogs extends App {
  generate()

  def generate(): Unit = {
    val apis: Vector[String] = Vector("departments", "orders", "users", "accounts", "admin", "status")

    val random = new Random
    val dtf: DateTimeFormatter = DateTimeFormat forPattern "dd-MM-yyyy HH:mm:ss"

    val logFile: File = File("streaming-kafka-course/src/main/resources/logs.txt").createFileIfNotExists(createParents = true).overwrite("")
    println(s"Created ${logFile.path}")

    val randomLog: IO[String] = IO {
      def i = random.nextInt(256)

      s"""$i.$i.$i.$i - [${DateTime.now.toString(dtf)}] "GET /${apis(random.nextInt(apis.length))} HTTP/1.1" 200"""
    }

    def log(s: String): String = {
      logFile appendLine s
      TimeUnit.SECONDS.sleep(5)
      s
    }

    val s = Stream repeatEval randomLog map log

    s.compile.drain.unsafeRunAsyncAndForget
  }
}