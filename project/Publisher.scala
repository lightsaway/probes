import sbt.Keys.{developers, homepage, licenses, organization, startYear}
import sbt.{Developer, url}

object Publisher {
  val publisher = List(
    organization := "com.github.lightsaway",
    startYear := Some(2019),
    homepage := Some(url("https://github.com/lightsaway/probes")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "lightsaway",
        "Anton Serhiienko",
        "a.a.sergienko@gmail.com",
        url("https://lightsaway.com")
      )
    ))
}
