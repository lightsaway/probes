name := "probes"
version := "0.1"

import Dependencies._

lazy val root = project.in(file(".")).
  aggregate(core, http4sProject, kafkaProject).
  settings(
    name := "probes",
    publishArtifact := false
  )

lazy val core = (project in file("modules/core"))
  .settings(
    Publisher.publisher,
    scalacOptions ++= ScalacOptions.default ,
    inThisBuild(
      List(
        scalaVersion := "2.13.1",
        scalafmtOnCompile := true,
        testOptions in Test += Tests.Argument("-oF"),
        javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
        parallelExecution := false
      )),
    name := "probes-core",
    libraryDependencies ++= Seq(
      fs2.core,
      slf4j.api,
      slf4j.log4jOver,
      logback.core % Test,
      logback.classic % Test,
      scalaTest
    ),
    scalafmtOnCompile := true,
  )


lazy val http4sProject = (project in file("modules/http4s-probes"))
  .dependsOn(core)
  .settings(
    Publisher.publisher,
    scalacOptions ++= ScalacOptions.default ,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    inThisBuild(
      List(
        scalaVersion := "2.13.1",
        scalafmtOnCompile := true,
        testOptions in Test += Tests.Argument("-oF"),
        javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
        parallelExecution := false
      )),
    name := "probes-http4s",
    libraryDependencies ++= Seq(
      http4s.client,
      http4s.dsl,
      scalaTest
    ),
    scalafmtOnCompile := true,
  )

lazy val kafkaProject = (project in file("modules/kafka-probes"))
  .dependsOn(core)
  .settings(
    Publisher.publisher,
    scalacOptions ++= ScalacOptions.default ,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    inThisBuild(
      List(
        scalaVersion := "2.13.1",
        scalafmtOnCompile := true,
        testOptions in Test += Tests.Argument("-oF"),
        javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
        parallelExecution := false
      )),
    name := "probes-kafka",
    libraryDependencies ++= Seq(
      kafka.clients,
      scalaTest,
      mockito
    ),
    scalafmtOnCompile := true,
  )