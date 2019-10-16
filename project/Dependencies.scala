import sbt._

object Dependencies {

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % Test
  lazy val mockito = "org.mockito" %% "mockito-scala-scalatest" % "1.6.2" % Test
  lazy val wiremock  = "com.github.tomakehurst" % "wiremock" % "1.33" % Test

  object fs2 {
    private val version = "2.0.0"

    lazy val core = "co.fs2" %% "fs2-core" % version
    lazy val io = "co.fs2" %% "fs2-io" % version
  }

  object slf4j {
    private val version = "1.7.25"

    val api = "org.slf4j" % "slf4j-api" % version
    val log4jOver = "org.slf4j" % "log4j-over-slf4j" % version
  }

  object logback {
    private val version = "1.2.3"

    val core = "ch.qos.logback" % "logback-core" % version
    val classic = "ch.qos.logback" % "logback-classic" % version
  }

  object http4s {
    private val version = "0.21.0-SNAPSHOT"
    val dsl = "org.http4s" %% "http4s-dsl" % version
    val server = "org.http4s" %% "http4s-blaze-server" % version
    val client = "org.http4s" %% "http4s-blaze-client" % version
  }

  object kafka {
    private val version = "2.3.0"
    val clients = "org.apache.kafka" % "kafka-clients"                     % version
  }

  object doobie{
    val version = "0.8.4"
    val core = "org.tpolecat" %% "doobie-core"      %  version
    val postgres = "org.tpolecat" %% "doobie-postgres"  %  version
  }

}