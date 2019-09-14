import sbt._

object Dependencies {
  lazy val dependencies: Seq[ModuleID] =
    Seq(
      backwards,
      scalatest, testcontainers, scalatestContainers, scribe, airframe, logging, pprint, pureConfig, betterFiles, apacheCommons,
      avro4s, circe, json4s,
      cats, monocle, shapeless, fs2, http4s, sttp, scalaUri, kafka, monixKafka, spark, sparkRestClient,
      twitter, elasticsearch, maxmindGioIp2,
      lombok // To prevent weird issue: java.lang.ClassNotFoundException: com.sun.tools.javac.code.TypeTags
    ).flatten

  lazy val backwards: Seq[ModuleID] = {
    val version = "1.0.28"

    Seq(
      "com.github.backwards-limited" % "scala-backwards" % version
    ) ++ Seq(
      "com.github.backwards-limited" % "scala-backwards" % version % "test, it" classifier "tests",
      "com.github.backwards-limited" % "scala-backwards" % version % "test, it" classifier "it"
    )
  }

  lazy val scalatest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test, it"
  )

  lazy val testcontainers: Seq[ModuleID] = Seq(
    "org.testcontainers" % "testcontainers" % "1.12.1" % "test, it"
  )
  
  lazy val scalatestContainers: Seq[ModuleID] = Seq(
    "com.dimafeng" %% "testcontainers-scala" % "0.32.0" % "test, it"
  )

  lazy val scribe: Seq[ModuleID] = Seq(
    "com.outr" %% "scribe" % "2.7.3"
  )

  lazy val airframe: Seq[ModuleID] = Seq(
    "org.wvlet.airframe" %% "airframe-log" % "19.9.6"
  )

  lazy val logging: Seq[ModuleID] = Seq(
    "org.slf4j" % "log4j-over-slf4j" % "1.7.28",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val pprint: Seq[ModuleID] = Seq(
    "com.lihaoyi" %% "pprint" % "0.5.5" % "test, it"
  )

  lazy val pureConfig: Seq[ModuleID] = {
    val version = "0.12.0"

    Seq(
      "com.github.pureconfig" %% "pureconfig",
      "com.github.pureconfig" %% "pureconfig-http4s"
    ).map(_ % version)
  }

  lazy val betterFiles: Seq[ModuleID] = Seq(
    "com.github.pathikrit" %% "better-files" % "3.8.0"
  )

  lazy val apacheCommons: Seq[ModuleID] = Seq(
    "org.apache.commons" % "commons-lang3" % "3.9"
  )

  lazy val avro4s: Seq[ModuleID] = Seq(
    "com.sksamuel.avro4s" %% "avro4s-core" % "3.0.1"
  )

  lazy val circe: Seq[ModuleID] = {
    val version = "0.12.1"

    Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-generic-extras",
      "io.circe" %% "circe-parser",
      "io.circe" %% "circe-refined"
    ).map(_ % version) ++ Seq(
      "io.circe" %% "circe-testing",
      "io.circe" %% "circe-literal"
    ).map(_ % version % "test, it")
  }

  lazy val json4s: Seq[ModuleID] = {
    val version = "3.6.7"
      
    Seq(
      "org.json4s" %% "json4s-jackson",
      "org.json4s" %% "json4s-ext"
    ).map(_ % version)
  }

  lazy val cats: Seq[ModuleID] = {
    val version = "2.0.0"

    Seq(
      "org.typelevel" %% "cats-core"
    ).map(_ % version) ++ Seq(
      "org.typelevel" %% "cats-effect" % "1.4.0"
    ) ++ Seq(
      "org.typelevel" %% "cats-laws",
      "org.typelevel" %% "cats-testkit"
    ).map(_ % version % "test, it")
  }
  
  lazy val monocle: Seq[ModuleID] = {
    val version = "2.0.0"

    Seq(
      "com.github.julien-truffaut" %% "monocle-core",
      "com.github.julien-truffaut" %% "monocle-macro",
      "com.github.julien-truffaut" %% "monocle-generic"
    ).map(_ % version) ++ Seq(
      "com.github.julien-truffaut" %% "monocle-law"
    ).map(_ % version % "test, it")
  }

  lazy val shapeless: Seq[ModuleID] = Seq(
    "com.chuusai" %% "shapeless" % "2.3.3"
  )
  
  lazy val fs2: Seq[ModuleID] = {
    val version = "2.0.0"
    
    Seq(
      "co.fs2" %% "fs2-core",
      "co.fs2" %% "fs2-io",
      "co.fs2" %% "fs2-reactive-streams"
    ).map(_ % version)
  }

  lazy val http4s: Seq[ModuleID] = {
    val version = "0.20.10"

    Seq(
      "org.http4s" %% "http4s-core",
      "org.http4s" %% "http4s-dsl",
      "org.http4s" %% "http4s-blaze-server",
      "org.http4s" %% "http4s-blaze-client",
      "org.http4s" %% "http4s-client",
      "org.http4s" %% "http4s-circe"
    ).map(_ % version) ++ Seq(
      "org.http4s" %% "http4s-testing",
      "org.http4s" %% "http4s-dsl"
    ).map(_ % version % "test, it")
  }

  lazy val sttp: Seq[ModuleID] = {
    val version = "1.6.6"

    Seq(
      "com.softwaremill.sttp" %% "core",
      "com.softwaremill.sttp" %% "circe"
    ).map(_ % version)
  }

  lazy val scalaUri: Seq[ModuleID] = Seq(
    "io.lemonlabs" %% "scala-uri" % "1.5.1"
  )

  lazy val kafka: Seq[ModuleID] = {
    val version = "2.3.0"
    
    Seq(
      "org.apache.kafka" % "kafka-clients",
      "org.apache.kafka" % "kafka-streams",
      "org.apache.kafka" %% "kafka-streams-scala"
    ).map(_ % version) ++ Seq(
      "org.apache.kafka" % "kafka-streams-test-utils"
    ).map(_ % version % "test, it")
  }

  lazy val monixKafka: Seq[ModuleID] = Seq(
    "io.monix" %% "monix-kafka-1x" % "1.0.0-RC4"
  )
  
  lazy val spark: Seq[ModuleID] = {
    val version = "2.4.2"
    
    Seq(
      "org.apache.spark" %% "spark-core",
      "org.apache.spark" %% "spark-streaming"
    ).map(_ % version)
  }
  
  lazy val sparkRestClient: Seq[ModuleID] = Seq(
    "com.github.ywilkof" % "spark-jobs-rest-client" % "1.3.9"
  )

  lazy val twitter: Seq[ModuleID] = Seq(
    "com.danielasfregola" %% "twitter4s" % "5.5",
    "com.twitter" % "hbc-core" % "2.2.0"
  )

  lazy val elasticsearch: Seq[ModuleID] = {
    val version = "6.7.1"

    Seq(
      "com.sksamuel.elastic4s" %% "elastic4s-core",
      "com.sksamuel.elastic4s" %% "elastic4s-http",
      "com.sksamuel.elastic4s" %% "elastic4s-http-streams"
    ).map(_ % version) ++ Seq(
      "com.sksamuel.elastic4s" %% "elastic4s-testkit",
      "com.sksamuel.elastic4s" %% "elastic4s-embedded"
    ).map(_ % version % "test, it")
  }

  lazy val maxmindGioIp2: Seq[ModuleID] = Seq(
    "com.maxmind.geoip2" % "geoip2" % "2.12.0"
  )

  lazy val lombok: Seq[ModuleID] = Seq(
    "org.projectlombok" % "lombok" % "1.18.10" % "provided"
  )
}