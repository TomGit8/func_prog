ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "ParsingSmadjUrb",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test // ou remplace par scalatest si tu préfères
    )
  )
