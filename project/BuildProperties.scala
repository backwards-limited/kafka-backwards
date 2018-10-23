import java.io.FileInputStream
import java.util.Properties
import sbt._

object BuildProperties {
  private lazy val properties = {
    val p = new Properties()
    p.load(new FileInputStream(file("project/build.properties").asFile))
    p
  }

  def apply(property: String): String = properties getProperty property
}