import scala.util.Random

def randomIp: String = {
  val random: Int => Int =
    Random.nextInt

  s"${random(223) + 1}.${(1 to 3).map(_ => random(255)).mkString(".")}"
}

randomIp