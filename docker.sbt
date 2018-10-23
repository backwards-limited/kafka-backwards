import scala.sys.process._

lazy val dockerComposeUp = taskKey[Unit]("Docker Compose Up with Build")

dockerComposeUp := {
  "sbt clean".!
  "sbt assembly".!
  "docker-compose up --build".!
}

lazy val dockerComposeServicesUp = taskKey[Unit]("Docker Compose Services Up")

dockerComposeServicesUp := {
  "docker-compose -f docker-compose-services.yml up".!
}

lazy val dockerComposeDown = taskKey[Unit]("Docker Compose Down")

dockerComposeDown := {
  "docker-compose down".!
}