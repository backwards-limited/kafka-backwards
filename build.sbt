import BuildProperties._
import Dependencies._
import sbt._

lazy val root = project("kafka-backwards", file("."))
  .aggregate(kafka, `beginners-course`, `master-realtime-stream-processing`)
  .settings(description := "Backwards Kafka module aggregation - Kafka functionality includes example usage in various courses")

lazy val kafka = project("kafka", file("kafka"))
  .settings(description := "Backwards Kafka functionality includes example usage in various courses")
  .settings(Test / javaOptions ++= Seq("-Dconfig.resource=application.test.conf"))

lazy val `beginners-course` = project("beginners-course", file("courses/beginners-course"))
  .dependsOn(kafka % "compile->compile;test->test;it->it")
  .settings(description := "Beginners Course - Apache Kafka Series")
  .settings(Test / javaOptions ++= Seq("-Dconfig.resource=application.test.conf"))

lazy val `master-realtime-stream-processing` = project("master-realtime-stream-processing", file("courses/master-realtime-stream-processing"))
  .settings(description := "Realtime Stream Processing Master Class Course")

lazy val IT = config("it") extend Test

// TODO - Somehow reuse from module "scala-backwards"
def project(id: String, base: File): Project =
  Project(id, base)
    .enablePlugins(DockerPlugin, DockerComposePlugin)
    .configs(IT)
    .settings(inConfig(IT)(Defaults.testSettings))
    .settings(Defaults.itSettings)
    .settings(
      ThisBuild / evictionErrorLevel := Level.Info,
      ThisBuild / turbo := true,
      scalaVersion := BuildProperties("scala.version"),
      sbtVersion := BuildProperties("sbt.version"),
      version := "0.1.0-SNAPSHOT",
      organization := "com.backwards",
      name := id,
      resolvers += "jitpack" at "https://jitpack.io",
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      libraryDependencies ++= dependencies,
      excludeDependencies ++= Seq("org.slf4j" % "slf4j-log4j12"),
      fork := true,
      IT / javaOptions ++= environment.map { case (key, value) => s"-D$key=$value" }.toSeq,
      scalacOptions ++= Seq(),
      assembly / assemblyJarName := s"$id-${version.value}.jar",
      assembly / test := {},
      assembly / assemblyMergeStrategy := {
        case PathList("javax", xs @ _*)  => MergeStrategy.first
        case PathList("org", xs @ _*)  => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith "module-info.class" => MergeStrategy.first
        case "application.conf"  => MergeStrategy.concat
        case x =>
          val oldStrategy = (assembly / assemblyMergeStrategy).value
          oldStrategy(x)
      }
    )
    .settings(
      // To use 'dockerComposeTest' to run tests in the 'IntegrationTest' scope instead of the default 'Test' scope:
      // 1) Package the tests that exist in the IntegrationTest scope
      testCasesPackageTask := (IntegrationTest / packageBin).value,
      // 2) Specify the path to the IntegrationTest jar produced in Step 1
      testCasesJar := (IntegrationTest /packageBin / artifactPath).value.getAbsolutePath,
      // 3) Include any IntegrationTest scoped resources on the classpath if they are used in the tests
      testDependenciesClasspath := {
        val fullClasspathCompile = (Compile / fullClasspath).value
        val classpathTestManaged = (IntegrationTest / managedClasspath).value
        val classpathTestUnmanaged = (IntegrationTest / unmanagedClasspath).value
        val testResources = (IntegrationTest / resources).value
        (fullClasspathCompile.files ++ classpathTestManaged.files ++ classpathTestUnmanaged.files ++ testResources).map(_.getAbsoluteFile).mkString(java.io.File.pathSeparator)
      },
      dockerImageCreationTask := docker.value
    )