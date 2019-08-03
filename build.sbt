import BuildProperties._
import Dependencies._
import sbt._

lazy val IT = config("it") extend Test

lazy val root = project("kafka-backwards", file("."))
  .settings(description := "Backwards Kafka module aggregation - Kafka functionality includes example usage in various courses")
  .aggregate(kafka, beginnersCourse, connectCourse, streamsCourse, streamingCourse)

lazy val kafka = project("kafka", file("kafka"))
  .settings(description := "Backwards Kafka functionality includes example usage in various courses")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))

lazy val beginnersCourse = project("beginners-course", file("courses/beginners-course"))
  .settings(description := "Beginners Course - Apache Kafka Series")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))
  .dependsOn(kafka % "compile->compile;test->test;it->it")

lazy val connectCourse = project("connect-course", file("courses/connect-course"))
  .settings(description := "Connect Course - Apache Kafka Series")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))
  .dependsOn(kafka % "compile->compile;test->test;it->it")

lazy val streamsCourse = project("streams-course", file("courses/streams-course"))
  .settings(description := "Streams Course - Apache Kafka Series")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))
  .dependsOn(kafka % "compile->compile;test->test;it->it")  

lazy val streamingCourse = project("streaming-kafka-course", file("courses/streaming-kafka-course"))
  .settings(description := "Kafka Streaming Course")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))
  .dependsOn(kafka % "compile->compile;test->test;it->it")

// TODO - Somehow reuse from module "scala-backwards"
def project(id: String, base: File): Project =
  Project(id, base)
    .configs(IT)
    .settings(inConfig(IT)(Defaults.testSettings))
    .settings(Defaults.itSettings)
    .settings(
      resolvers ++= Seq(
        Resolver.sonatypeRepo("releases"),
        Resolver.bintrayRepo("cakesolutions", "maven"),
        "jitpack" at "https://jitpack.io",
        "Confluent Platform Maven" at "http://packages.confluent.io/maven/"
      ),
      scalaVersion := BuildProperties("scala.version"),
      sbtVersion := BuildProperties("sbt.version"),
      organization := "com.backwards",
      name := id,
      autoStartServer := false,
      addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.10"),
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
      libraryDependencies ++= dependencies,
      excludeDependencies ++= Seq("org.slf4j" % "slf4j-log4j12"),
      fork := true,
      javaOptions in IT ++= environment.map { case (key, value) => s"-D$key=$value" }.toSeq,
      scalacOptions ++= Seq("-Ypartial-unification"),
      assemblyJarName in assembly := s"$id.jar",
      test in assembly := {},
      assemblyMergeStrategy in assembly := {
        case PathList("javax", xs @ _*)  => MergeStrategy.first
        case PathList("org", xs @ _*)  => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith "module-info.class" => MergeStrategy.first
        case "application.conf"  => MergeStrategy.concat
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      }
    )