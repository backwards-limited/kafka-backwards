package com.backwards.streaming.kafka

import java.util.concurrent.TimeUnit
import scala.util.Random
import better.files.File
import cats.effect.IO
import fs2.Stream
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object GenLogs extends App {
  generate()

  def generate(): Unit = {
    val apis: Vector[String] = Vector("departments", "orders", "users", "accounts", "admin", "status")
    val departments: Vector[String] = Vector("finance", "hr", "it", "recruitment")

    val random = new Random
    val dtf: DateTimeFormatter = DateTimeFormat forPattern "dd-MM-yyyy HH:mm:ss"

    val logFile: File = File("streaming-kafka-course/src/main/resources/logs.txt").createFileIfNotExists(createParents = true).overwrite("")
    println(s"Created ${logFile.path}")

    val randomLog: IO[String] = IO {
      def i = random.nextInt(256)

      val api = {
        val api = apis(random.nextInt(apis.length))

        if (api == "departments") {
          s"$api/${departments(random.nextInt(departments.length))}"
        } else {
          api
        }
      }

      s"""$i.$i.$i.$i - [${DateTime.now.toString(dtf)}] "GET /$api HTTP/1.1" 200"""
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
