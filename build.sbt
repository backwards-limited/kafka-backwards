import Dependencies._
import sbt._

lazy val root = project("kafka-backwards", file("."))
  .settings(
    description := "Backwards Kafka module aggregation - Kafka functionality includes example usage in various courses",
    assemblyJarName in assembly := "kafka-backwards.jar"
  )
  .aggregate(
    backwards, kafka, beginnersCourse
  )

lazy val backwards = project("backwards") // TODO - Extract this into its own repo
  .settings(
    description := "Scala functionality by Backwards - reusable functionality such as testing, logging",
    assemblyJarName in assembly := "backwards.jar"
  )

lazy val kafka = project("kafka")
  .settings(
    description := "Backwards Kafka functionality includes example usage in various courses",
    assemblyJarName in assembly := "kafka.jar"
  )
  .dependsOn(
    backwards % "compile->compile;test->test;it->it"
  )

lazy val beginnersCourse = project("beginners-course")
  .settings(
    description := "Beginners Course - Apache Kafka Series",
    assemblyJarName in assembly := "beginners-course.jar"
  )
  .dependsOn(
    kafka % "compile->compile;test->test;it->it"
  )

def project(id: String): Project = project(id, file(id))

def project(id: String, base: File): Project =
  Project(id, base)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)
    .settings(
      resolvers ++= Seq(
        "Confluent Platform Maven" at "http://packages.confluent.io/maven/",
        Resolver.bintrayRepo("cakesolutions", "maven"),
        "jitpack" at "https://jitpack.io"
      ),
      scalaVersion := BuildProperties("scala.version"),
      sbtVersion := BuildProperties("sbt.version"),
      organization := "com.backwards",
      name := id,
      libraryDependencies ++= dependencies,
      fork in Test := true,
      fork in IntegrationTest := true,
      fork in run := true,
      scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits"),
      assemblyMergeStrategy in assembly := {
        case PathList("javax", "servlet", xs @ _*)          => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".html"  => MergeStrategy.first
        case "application.conf"                             => MergeStrategy.concat
        case "io.netty.versions.properties"                 => MergeStrategy.concat
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      }
    )