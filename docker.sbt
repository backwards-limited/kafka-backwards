import scala.sys.process._

lazy val dockerComposeUp = taskKey[Unit]("Docker Compose Up with Build")

dockerComposeUp := {
  "sbt clean".!
  "sbt assembly".!
  "docker-compose up --build".!
}

lazy val dockerComposeDown = taskKey[Unit]("Docker Compose Down")

dockerComposeDown := {
  "docker-compose down".!
}