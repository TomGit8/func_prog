ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "ParsingSmadjUrb",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalafx" %% "scalafx" % "16.0.0-R25",
      "org.openjfx" % "javafx-base" % "17.0.1",
      "org.openjfx" % "javafx-controls" % "17.0.1",
      "org.openjfx" % "javafx-graphics" % "17.0.1",
      "org.openjfx" % "javafx-media" % "17.0.1"  // Ajout de JavaFX Media
    )
  )
