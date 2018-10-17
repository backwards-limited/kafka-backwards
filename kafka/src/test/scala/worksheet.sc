import java.util.Map.Entry
import java.util.Properties
import scala.language.implicitConversions
import monocle.macros.syntax.lens._

object Configuration {
  implicit def toProperties(c: Configuration): Properties = c.toProperties
}

case class Configuration(topic: String, properties: Map[String, String] = Map.empty[String, String]) {
  lazy val toProperties: Properties = (new Properties /: properties) { case (p, (k, v)) =>
    p.put(k, v)
    p
  }

  def + (kv: (String, String)): Configuration =
    this.lens(_.properties).modify(_ + kv)

  def + (key: String, value: String): Configuration = this + (key -> value)
}

val c = Configuration("blah") + ("scooby" -> "doo")

print(c)

print(c.properties)