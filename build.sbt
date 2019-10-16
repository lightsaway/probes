name := "lightsaway.probes"
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
    name := "probes-core",
    libraryDependencies ++= Seq(
      fs2.core
    )
  ).settings(Settings.default: _*)

lazy val http4sProject = (project in file("modules/http4s-probes"))
  .dependsOn(core)
  .settings(
    name := "probes-http4s",
    libraryDependencies ++= Seq(
      http4s.client,
      http4s.dsl
    )
  ).settings(Settings.default: _*)

lazy val kafkaProject = (project in file("modules/kafka-probes"))
  .dependsOn(core)
  .settings(
    name := "probes-kafka",
    libraryDependencies ++= Seq(
      kafka.clients
    ),
  ).settings(Settings.default: _*)


lazy val pgProject = (project in file("modules/postgres-probes"))
  .dependsOn(core)
  .settings(
    name := "probes-postgres",
    libraryDependencies ++= Seq(
      doobie.core,
      doobie.postgres
    ),
  ).settings(Settings.default: _*)


