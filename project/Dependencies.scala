import sbt._

object Dependencies {
  lazy val dependencies: Seq[ModuleID] =
    Seq(
      scalatest, testcontainers, airframe, logging, pprint, configuration, betterFiles, apacheCommons,
      avro4s, circe, json4s,
      cats, monocle, shapeless, http4s, scalaUri, kafka, twitter, elasticsearch
    ).flatten

  lazy val scalatest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % "test, it"
  )

  lazy val testcontainers: Seq[ModuleID] = Seq(
    "org.testcontainers" % "testcontainers" % "1.9.1" % "test, it"
  )

  lazy val airframe: Seq[ModuleID] = Seq(
    "org.wvlet.airframe" %% "airframe-log" % "0.69"
  )

  lazy val logging: Seq[ModuleID] = Seq(
    "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val pprint: Seq[ModuleID] = Seq(
    "com.lihaoyi" %% "pprint" % "0.5.3" % "test, it"
  )

  lazy val configuration: Seq[ModuleID] = {
    val version = "0.9.2"

    Seq(
      "com.github.pureconfig" %% "pureconfig",
      "com.github.pureconfig" %% "pureconfig-http4s"
    ).map(_ % version)
  }

  lazy val betterFiles: Seq[ModuleID] = Seq(
    "com.github.pathikrit" %% "better-files" % "3.6.0"
  )

  lazy val apacheCommons: Seq[ModuleID] = Seq(
    "org.apache.commons" % "commons-lang3" % "3.8.1"
  )

  lazy val avro4s: Seq[ModuleID] = Seq(
    "com.sksamuel.avro4s" %% "avro4s-core" % "2.0.2"
  )

  lazy val circe: Seq[ModuleID] = {
    val version = "0.10.1"

    Seq(
      "io.circe" %% "circe-testing",
      "io.circe" %% "circe-literal"
    ).map(_ % version % "test, it") ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-generic-extras",
      "io.circe" %% "circe-parser"
    ).map(_ % version)
  }

  lazy val json4s: Seq[ModuleID] = {
    val version = "3.6.2"
      
    Seq(
      "org.json4s" %% "json4s-jackson",
      "org.json4s" %% "json4s-ext"
    ).map(_ % version)
  }

  lazy val cats: Seq[ModuleID] = {
    val version = "1.4.0"

    Seq(
      "org.typelevel" %% "cats-laws",
      "org.typelevel" %% "cats-testkit"
    ).map(_ % version % "test, it") ++ Seq(
      "org.typelevel" %% "cats-core"
    ).map(_ % version) ++ Seq(
      "org.typelevel" %% "cats-effect" % "1.0.0"
    )
  }
  
  lazy val monocle: Seq[ModuleID] = {
    val version = "1.5.0"

    Seq(
      "com.github.julien-truffaut" %% "monocle-law"
    ).map(_ % version % "test, it") ++ Seq(
      "com.github.julien-truffaut" %% "monocle-core",
      "com.github.julien-truffaut" %% "monocle-macro",
      "com.github.julien-truffaut" %% "monocle-generic"
    ).map(_ % version)
  }

  lazy val shapeless: Seq[ModuleID] = Seq(
    "com.chuusai" %% "shapeless" % "2.3.3"
  )

  lazy val http4s: Seq[ModuleID] = {
    val version = "0.19.0"

    Seq(
      "org.http4s" %% "http4s-testing",
      "org.http4s" %% "http4s-dsl"
    ).map(_ % version % "test, it") ++ Seq(
      "org.http4s" %% "http4s-core",
      "org.http4s" %% "http4s-blaze-server",
      "org.http4s" %% "http4s-blaze-client",
      "org.http4s" %% "http4s-client",
      "org.http4s" %% "http4s-circe"
    ).map(_ % version)
  }

  lazy val scalaUri: Seq[ModuleID] = Seq(
    "io.lemonlabs" %% "scala-uri" % "1.3.1"
  )

  lazy val kafka: Seq[ModuleID] = {
    val version = "2.0.0"
    
    Seq(
      "org.apache.kafka" % "kafka-clients",
      "org.apache.kafka" % "kafka-streams"
    ).map(_ % version)
  }

  lazy val twitter: Seq[ModuleID] = Seq(
    "com.danielasfregola" %% "twitter4s" % "5.5",
    "com.twitter" % "hbc-core" % "2.2.0"
  )

  lazy val elasticsearch: Seq[ModuleID] = {
    val version = "6.3.7"

    Seq(
      "com.sksamuel.elastic4s" %% "elastic4s-testkit",
      "com.sksamuel.elastic4s" %% "elastic4s-embedded"
    ).map(_ % version % "test, it") ++ Seq(
      "com.sksamuel.elastic4s" %% "elastic4s-core",
      "com.sksamuel.elastic4s" %% "elastic4s-http",
      "com.sksamuel.elastic4s" %% "elastic4s-http-streams"
    ).map(_ % version)
  }
}