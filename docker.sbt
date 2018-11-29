import scala.sys.process._
import sbt.internal.util.complete.DefaultParsers._

lazy val dockerComposeUp = inputKey[Unit]("Docker Compose Up")

dockerComposeUp := {
  "sbt clean".!
  "sbt assembly".!

  val args = spaceDelimited("<arg>").parsed

  val s = streams.value
  s.log.info(s"SBT args = $args")

  doDockerComposeUp("docker-compose.yml")(args)
}

lazy val dockerComposeServicesUp = inputKey[Unit]("Docker Compose Services Up")

dockerComposeServicesUp := {
  val args = spaceDelimited("<arg>").parsed

  val s = streams.value
  s.log.info(s"SBT args = $args")

  doDockerComposeUp("docker-compose-services.yml")(args)
}

lazy val dockerComposeDown = inputKey[Unit]("Docker Compose Down")

dockerComposeDown := {
  spaceDelimited("<arg>").parsed match {
    case Seq(module) =>
      Process("docker-compose down", file(s"./$module")).!!

    case _ =>
      "docker-compose down".!
  }
}

lazy val doDockerComposeUp: String => Seq[String] => Int =
  dockerComposeFileName => {
    case Seq(module) if !module.contains("/") =>
      s"docker-compose -f $module/$dockerComposeFileName up --build".!

    case args if args.contains("-f") =>
      s"docker-compose ${args.mkString(" ")} up --build".!

    case args =>
      s"docker-compose -f ${args.mkString(" ")} up --build".!
  }