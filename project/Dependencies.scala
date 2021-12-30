import sbt._

object Dependencies {
  lazy val dependencies: Seq[ModuleID] =
    List(
      scalatest, scalatestContainers, scribe, pprint, pureConfig, log4Cats, logback,
      cats, catsEffect, catsEffectTime,
      monocle, shapeless, fs2, scalaUri, kafka, monixKafka, kafkaSerde, betterFiles, decline,
      twitter, elasticsearch, apacheCommons,
      newtype, tagging, avro4s, circe, json4s,
      scalaBackwards
    ).flatten

  lazy val scalatest: Seq[ModuleID] =
    List("org.scalatest" %% "scalatest" % "3.2.10" % "test, it")

  lazy val scalatestContainers: Seq[ModuleID] = {
    val group = "com.dimafeng"
    val version = "1.0.0-alpha1"

    List(
      "testcontainers-scala-scalatest", "testcontainers-scala-kafka", "testcontainers-scala-mysql", "testcontainers-scala-postgresql"
    ).map(group %% _ % version % "test, it" withSources() withJavadoc())
  }

  lazy val scribe: Seq[ModuleID] =
    List("com.outr" %% "scribe" % "3.6.4")

  lazy val pprint: Seq[ModuleID] =
    List("com.lihaoyi" %% "pprint" % "0.7.1" % "test, it")

  lazy val pureConfig: Seq[ModuleID] = {
    val version = "0.17.1"

    List(
      "com.github.pureconfig" %% "pureconfig",
      "com.github.pureconfig" %% "pureconfig-http4s"
    ).map(_ % version)
  }

  lazy val betterFiles: Seq[ModuleID] =
    List("com.github.pathikrit" %% "better-files" % "3.9.1")

  lazy val decline: Seq[ModuleID] = {
    val group = "com.monovore"
    val version = "2.2.0"

    List(
      "decline", "decline-effect"
    ).map(group %% _ % version)
  }

  lazy val twitter: Seq[ModuleID] =
    List(
      "com.danielasfregola" %% "twitter4s" % "7.0",
      "com.twitter" % "hbc-core" % "2.2.0"
    )

  lazy val elasticsearch: Seq[ModuleID] = {
    val group = "com.sksamuel.elastic4s"
    val version = "6.7.8"

    List(
      "elastic4s-core", "elastic4s-http-streams", "elastic4s-http"
    ).map(group %% _ % version) ++ List(
      "elastic4s-testkit", "elastic4s-embedded"
    ).map(group %% _ % version % "test, it")
  }

  lazy val apacheCommons: Seq[ModuleID] =
    List("org.apache.commons" % "commons-lang3" % "3.12.0")

  lazy val newtype: Seq[ModuleID] =
    List("io.estatico" %% "newtype" % "0.4.4")

  lazy val tagging: Seq[ModuleID] =
    List("com.softwaremill.common" %% "tagging" % "2.3.2")

  lazy val avro4s: Seq[ModuleID] =
    List("com.sksamuel.avro4s" %% "avro4s-core" % "4.0.12")

  lazy val circe: Seq[ModuleID] = {
    val group = "io.circe"
    val version = "0.14.1"

    List(
      "circe-core", "circe-generic", "circe-generic-extras", "circe-parser", "circe-refined"
    ).map(group %% _ % version) ++ List(
      "circe-testing", "circe-literal"
    ).map(group %% _ % version % "test, it")
  }

  lazy val json4s: Seq[ModuleID] = {
    val version = "4.0.3"

    List(
      "org.json4s" %% "json4s-jackson",
      "org.json4s" %% "json4s-ext"
    ).map(_ % version)
  }

  lazy val log4Cats: Seq[ModuleID] = {
    val group = "org.typelevel"
    val version = "2.1.1"

    List(
      "log4cats-core", "log4cats-slf4j"
    ).map(group %% _ % version)
  }

  lazy val logback: Seq[ModuleID] =
    List("ch.qos.logback" % "logback-classic" % "1.2.10")

  lazy val cats: Seq[ModuleID] = {
    val group = "org.typelevel"
    val version = "2.7.0"

    List(
      "cats-core", "cats-free"
    ).map(group %% _ % version withSources() withJavadoc()) ++ List(
      "cats-laws", "cats-testkit"
    ).map(group %% _ % version % "test, it" withSources() withJavadoc()) ++ List(
      "cats-mtl"
    ).map(group %% _ % "1.2.1" withSources() withJavadoc())
  }

  lazy val catsEffect: Seq[ModuleID] =
    List("org.typelevel" %% "cats-effect" % "3.3.0")

  lazy val catsEffectTime: Seq[ModuleID] =
    List("io.chrisdavenport" %% "cats-effect-time" % "0.2.0")
  
  lazy val monocle: Seq[ModuleID] = {
    val group = "com.github.julien-truffaut"
    val version = "2.1.0"

    List(
      "monocle-core", "monocle-macro", "monocle-generic"
    ).map(group %% _ % version) ++ List(
      "monocle-law"
    ).map(group %% _ % version % "test, it")
  }

  lazy val shapeless: Seq[ModuleID] =
    List("com.chuusai" %% "shapeless" % "2.3.7")
  
  lazy val fs2: Seq[ModuleID] = {
    val group = "co.fs2"
    val version = "3.2.2"

    List(
      "fs2-core", "fs2-io", "fs2-reactive-streams"
    ).map(group %% _ % version)
  }

  lazy val scalaUri: Seq[ModuleID] =
    List("io.lemonlabs" %% "scala-uri" % "3.6.0")

  lazy val kafka: Seq[ModuleID] = {
    val group = "org.apache.kafka"
    val version = "3.0.0"

    List(
      group % "kafka-clients",
      group % "kafka-streams",
      group %% "kafka-streams-scala"
    ).map(_ % version) ++ List(
      group % "kafka-streams-test-utils"
    ).map(_ % version % "test, it")
  }

  lazy val monixKafka: Seq[ModuleID] =
    List("io.monix" %% "monix-kafka-1x" % "1.0.0-RC7")

  lazy val kafkaSerde: Seq[ModuleID] = {
    val group = "io.github.azhur"
    val version = "0.5.0"

    List(
      "kafka-serde-circe", "kafka-serde-avro4s"
    ).map(group %% _ % version)
  }

  lazy val scalaBackwards: Seq[ModuleID] = {
    val group = "com.github.backwards-limited.scala-backwards"
    val version = "1.1.8"

    List(
      group % "main_2.13" % version
    ) ++ List(
      group % "main_2.13" % version % "test, it" classifier "tests",
      group % "main_2.13" % version % "test, it" classifier "it"
    )
  }
}