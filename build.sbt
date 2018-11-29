import BuildProperties._
import Dependencies._
import sbt._

lazy val root = project("kafka-backwards", file("."))
  .settings(description := "Backwards Kafka module aggregation - Kafka functionality includes example usage in various courses")
  .aggregate(kafka, beginnersCourse)

lazy val kafka = project("kafka")
  .settings(description := "Backwards Kafka functionality includes example usage in various courses")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))

lazy val beginnersCourse = project("beginners-course")
  .settings(description := "Beginners Course - Apache Kafka Series")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))
  .dependsOn(kafka % "compile->compile;test->test;it->it")

def project(id: String): Project = project(id, file(id))

// TODO - Somehow reuse from module "scala-backwards"
def project(id: String, base: File): Project =
  Project(id, base)
    .configs(IntegrationTest)
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
      addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
      libraryDependencies ++= dependencies,
      fork in Test := true,
      fork in IntegrationTest := true,
      javaOptions in IntegrationTest ++= environment.map { case (key, value) => s"-D$key=$value" }.toSeq,
      scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits"),
      assemblyJarName in assembly := s"$id.jar",
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