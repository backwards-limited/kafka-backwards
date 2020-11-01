import sbt._

object Dependencies {
  lazy val dependencies: Seq[ModuleID] =
    Seq(
      scalatest, scalatestContainers, scribe, pprint, pureConfig, log4Cats, logback,
      cats, catsEffectTime,
      monocle, shapeless, fs2, http4s, sttp, scalaUri, kafka, monixKafka, circeKafka, betterFiles, decline,
      twitter, elasticsearch, apacheCommons,
      newtype, tagging, avro4s, circe, json4s,
      scalaBackwards
    ).flatten

  lazy val scalatest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.2" % "test, it"
  )
  
  lazy val scalatestContainers: Seq[ModuleID] = Seq(
    "com.dimafeng" %% "testcontainers-scala" % "0.38.4" % "test, it"
  )

  lazy val scribe: Seq[ModuleID] = Seq(
    "com.outr" %% "scribe" % "2.8.1"
  )

  lazy val pprint: Seq[ModuleID] = Seq(
    "com.lihaoyi" %% "pprint" % "0.6.0" % "test, it"
  )

  lazy val pureConfig: Seq[ModuleID] = {
    val version = "0.14.0"

    Seq(
      "com.github.pureconfig" %% "pureconfig",
      "com.github.pureconfig" %% "pureconfig-http4s"
    ).map(_ % version)
  }

  lazy val betterFiles: Seq[ModuleID] = Seq(
    "com.github.pathikrit" %% "better-files" % "3.9.1"
  )

  lazy val decline: Seq[ModuleID] = {
    val group = "com.monovore"
    val version = "1.3.0"

    Seq(
      "decline", "decline-effect"
    ).map(group %% _ % version)
  }

  lazy val twitter: Seq[ModuleID] = Seq(
    "com.danielasfregola" %% "twitter4s" % "7.0",
    "com.twitter" % "hbc-core" % "2.2.0"
  )

  lazy val elasticsearch: Seq[ModuleID] = {
    val group = "com.sksamuel.elastic4s"
    val version = "6.7.8"

    Seq(
      "elastic4s-core", "elastic4s-http", "elastic4s-http-streams"
    ).map(group %% _ % version) ++ Seq(
      "elastic4s-testkit", "elastic4s-embedded"
    ).map(group %% _ % version % "test, it")
  }

  lazy val apacheCommons: Seq[ModuleID] = Seq(
    "org.apache.commons" % "commons-lang3" % "3.11"
  )

  lazy val newtype: Seq[ModuleID] = Seq(
    "io.estatico" %% "newtype" % "0.4.4"
  )

  lazy val tagging: Seq[ModuleID] = Seq(
    "com.softwaremill.common" %% "tagging" % "2.2.1"
  )

  lazy val avro4s: Seq[ModuleID] = Seq(
    "com.sksamuel.avro4s" %% "avro4s-core" % "4.0.0"
  )

  lazy val circe: Seq[ModuleID] = {
    val group = "io.circe"
    val version = "0.13.0"

    Seq(
      "circe-core", "circe-generic", "circe-generic-extras", "circe-parser", "circe-refined"
    ).map(group %% _ % version) ++ Seq(
      "circe-testing", "circe-literal"
    ).map(group %% _ % version % "test, it")
  }

  lazy val json4s: Seq[ModuleID] = {
    val version = "3.6.10"
      
    Seq(
      "org.json4s" %% "json4s-jackson",
      "org.json4s" %% "json4s-ext"
    ).map(_ % version)
  }

  lazy val log4Cats: Seq[ModuleID] = {
    val group = "io.chrisdavenport"
    val version = "1.1.1"

    Seq(
      "log4cats-core", "log4cats-slf4j"
    ).map(group %% _ % version)
  }

  lazy val logback: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val cats: Seq[ModuleID] = {
    val group = "org.typelevel"
    val version = "2.2.0"

    Seq(
      "cats-core", "cats-effect"
    ).map(group %% _ % version) ++ Seq(
      "cats-laws", "cats-testkit"
    ).map(group %% _ % version % "test, it")
  }

  lazy val catsEffectTime: Seq[ModuleID] = Seq(
    "io.chrisdavenport" %% "cats-effect-time" % "0.1.2"
  )
  
  lazy val monocle: Seq[ModuleID] = {
    val group = "com.github.julien-truffaut"
    val version = "2.1.0"

    Seq(
      "monocle-core", "monocle-macro", "monocle-generic"
    ).map(group %% _ % version) ++ Seq(
      "monocle-law"
    ).map(group %% _ % version % "test, it")
  }

  lazy val shapeless: Seq[ModuleID] = Seq(
    "com.chuusai" %% "shapeless" % "2.3.3"
  )
  
  lazy val fs2: Seq[ModuleID] = {
    val group = "co.fs2"
    val version = "2.4.4"
    
    Seq(
      "fs2-core", "fs2-io", "fs2-reactive-streams"
    ).map(group %% _ % version)
  }

  lazy val http4s: Seq[ModuleID] = {
    val group = "org.http4s"
    val version = "0.21.7"

    Seq(
      "http4s-core", "http4s-dsl", "http4s-blaze-server", "http4s-blaze-client", "http4s-client", "http4s-circe"
    ).map(group %% _ % version) ++ Seq(
      "http4s-testing", "http4s-dsl"
    ).map(group %% _ % version % "test, it")
  }

  lazy val sttp: Seq[ModuleID] = {
    val version = "1.7.2"

    Seq(
      "com.softwaremill.sttp" %% "core",
      "com.softwaremill.sttp" %% "circe"
    ).map(_ % version)
  }

  lazy val scalaUri: Seq[ModuleID] = Seq(
    "io.lemonlabs" %% "scala-uri" % "2.3.1"
  )

  lazy val kafka: Seq[ModuleID] = {
    val version = "2.6.0"
    
    Seq(
      "org.apache.kafka" % "kafka-clients",
      "org.apache.kafka" % "kafka-streams",
      "org.apache.kafka" %% "kafka-streams-scala"
    ).map(_ % version) ++ Seq(
      "org.apache.kafka" % "kafka-streams-test-utils"
    ).map(_ % version % "test, it")
  }

  lazy val monixKafka: Seq[ModuleID] = Seq(
    "io.monix" %% "monix-kafka-1x" % "1.0.0-RC6"
  )

  lazy val circeKafka: Seq[ModuleID] = Seq(
    "io.github.azhur" %% "kafka-serde-circe" % "0.5.0"
  )

  lazy val scalaBackwards: Seq[ModuleID] = {
    val group = "com.github.backwards-limited"
    val version = "1.0.29"

    Seq(
      group % "scala-backwards" % version
    ) ++ Seq(
      group % "scala-backwards" % version % "test, it" classifier "tests",
      group % "scala-backwards" % version % "test, it" classifier "it"
    )
  }
}