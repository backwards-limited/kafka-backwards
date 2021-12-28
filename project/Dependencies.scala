import scala.collection.immutable.LinearSeq
import sbt._

object Dependencies {
  lazy val dependencies: LinearSeq[ModuleID] =
    LinearSeq(
      scalatest, scalatestContainers, scribe, pprint, pureConfig, log4Cats, logback,
      cats, catsEffect, catsEffectTime,
      monocle, shapeless, fs2, scalaUri, kafka, monixKafka, kafkaSerde, betterFiles, decline,
      twitter, elasticsearch, apacheCommons,
      newtype, tagging, avro4s, circe, json4s,
      scalaBackwards
    ).flatten

  lazy val scalatest: LinearSeq[ModuleID] =
    LinearSeq("org.scalatest" %% "scalatest" % "3.2.10" % "test, it")

  lazy val scalatestContainers: LinearSeq[ModuleID] = {
    val group = "com.dimafeng"
    val version = "1.0.0-alpha1"

    LinearSeq(
      "testcontainers-scala-scalatest", "testcontainers-scala-kafka", "testcontainers-scala-mysql", "testcontainers-scala-postgresql"
    ).map(group %% _ % version % "test, it" withSources() withJavadoc())
  }

  lazy val scribe: LinearSeq[ModuleID] =
    LinearSeq("com.outr" %% "scribe" % "3.6.3")

  lazy val pprint: LinearSeq[ModuleID] =
    LinearSeq("com.lihaoyi" %% "pprint" % "0.6.6" % "test, it")

  lazy val pureConfig: LinearSeq[ModuleID] = {
    val version = "0.17.1"

    LinearSeq(
      "com.github.pureconfig" %% "pureconfig",
      "com.github.pureconfig" %% "pureconfig-http4s"
    ).map(_ % version)
  }

  lazy val betterFiles: LinearSeq[ModuleID] =
    LinearSeq("com.github.pathikrit" %% "better-files" % "3.9.1")

  lazy val decline: LinearSeq[ModuleID] = {
    val group = "com.monovore"
    val version = "2.2.0"

    LinearSeq(
      "decline", "decline-effect"
    ).map(group %% _ % version)
  }

  lazy val twitter: LinearSeq[ModuleID] =
    LinearSeq(
      "com.danielasfregola" %% "twitter4s" % "7.0",
      "com.twitter" % "hbc-core" % "2.2.0"
    )

  lazy val elasticsearch: LinearSeq[ModuleID] = {
    val group = "com.sksamuel.elastic4s"
    val version = "6.7.8"

    LinearSeq(
      "elastic4s-core", "elastic4s-http-streams", "elastic4s-http"
    ).map(group %% _ % version) ++ LinearSeq(
      "elastic4s-testkit", "elastic4s-embedded"
    ).map(group %% _ % version % "test, it")
  }

  lazy val apacheCommons: LinearSeq[ModuleID] =
    LinearSeq("org.apache.commons" % "commons-lang3" % "3.12.0")

  lazy val newtype: LinearSeq[ModuleID] =
    LinearSeq("io.estatico" %% "newtype" % "0.4.4")

  lazy val tagging: LinearSeq[ModuleID] =
    LinearSeq("com.softwaremill.common" %% "tagging" % "2.3.2")

  lazy val avro4s: LinearSeq[ModuleID] =
    LinearSeq("com.sksamuel.avro4s" %% "avro4s-core" % "4.0.11")

  lazy val circe: LinearSeq[ModuleID] = {
    val group = "io.circe"
    val version = "0.14.1"

    LinearSeq(
      "circe-core", "circe-generic", "circe-generic-extras", "circe-parser", "circe-refined"
    ).map(group %% _ % version) ++ LinearSeq(
      "circe-testing", "circe-literal"
    ).map(group %% _ % version % "test, it")
  }

  lazy val json4s: LinearSeq[ModuleID] = {
    val version = "4.0.3"
      
    LinearSeq(
      "org.json4s" %% "json4s-jackson",
      "org.json4s" %% "json4s-ext"
    ).map(_ % version)
  }

  lazy val log4Cats: LinearSeq[ModuleID] = {
    val group = "org.typelevel"
    val version = "2.1.1"

    LinearSeq(
      "log4cats-core", "log4cats-slf4j"
    ).map(group %% _ % version)
  }

  lazy val logback: LinearSeq[ModuleID] =
    LinearSeq("ch.qos.logback" % "logback-classic" % "1.2.7")

  lazy val cats: LinearSeq[ModuleID] = {
    val group = "org.typelevel"
    val version = "2.7.0"

    LinearSeq(
      "cats-core", "cats-free"
    ).map(group %% _ % version withSources() withJavadoc()) ++ LinearSeq(
      "cats-laws", "cats-testkit"
    ).map(group %% _ % version % "test, it" withSources() withJavadoc()) ++ LinearSeq(
      "cats-mtl"
    ).map(group %% _ % "1.2.1" withSources() withJavadoc())
  }

  lazy val catsEffect: LinearSeq[ModuleID] =
    LinearSeq("org.typelevel" %% "cats-effect" % "3.3.0")

  lazy val catsEffectTime: LinearSeq[ModuleID] =
    LinearSeq("io.chrisdavenport" %% "cats-effect-time" % "0.2.0")
  
  lazy val monocle: LinearSeq[ModuleID] = {
    val group = "com.github.julien-truffaut"
    val version = "2.1.0"

    LinearSeq(
      "monocle-core", "monocle-macro", "monocle-generic"
    ).map(group %% _ % version) ++ LinearSeq(
      "monocle-law"
    ).map(group %% _ % version % "test, it")
  }

  lazy val shapeless: LinearSeq[ModuleID] =
    LinearSeq("com.chuusai" %% "shapeless" % "2.3.7")
  
  lazy val fs2: LinearSeq[ModuleID] = {
    val group = "co.fs2"
    val version = "3.2.2"
    
    LinearSeq(
      "fs2-core", "fs2-io", "fs2-reactive-streams"
    ).map(group %% _ % version)
  }

  lazy val scalaUri: LinearSeq[ModuleID] =
    LinearSeq("io.lemonlabs" %% "scala-uri" % "3.6.0")

  lazy val kafka: LinearSeq[ModuleID] = {
    val group = "org.apache.kafka"
    val version = "3.0.0"
    
    LinearSeq(
      group % "kafka-clients",
      group % "kafka-streams",
      group %% "kafka-streams-scala"
    ).map(_ % version) ++ LinearSeq(
      group % "kafka-streams-test-utils"
    ).map(_ % version % "test, it")
  }

  lazy val monixKafka: LinearSeq[ModuleID] =
    LinearSeq("io.monix" %% "monix-kafka-1x" % "1.0.0-RC7")

  lazy val kafkaSerde: LinearSeq[ModuleID] = {
    val group = "io.github.azhur"
    val version = "0.5.0"

    LinearSeq(
      "kafka-serde-circe", "kafka-serde-avro4s"
    ).map(group %% _ % version)
  }

  lazy val scalaBackwards: LinearSeq[ModuleID] = {
    val group = "com.github.backwards-limited.scala-backwards"
    val version = "1.1.8"

    LinearSeq(
      group % "main_2.13" % version
    ) ++ LinearSeq(
      group % "main_2.13" % version % "test, it" classifier "tests",
      group % "main_2.13" % version % "test, it" classifier "it"
    )
  }
}