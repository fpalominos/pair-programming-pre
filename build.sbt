import Dependencies._

ThisBuild / scalaVersion     := "2.12.17"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

scalacOptions += "-Ypartial-unification"

lazy val root = (project in file("."))
  .settings(
    name := "pair-programming-prep",
    libraryDependencies += catsCore,
    libraryDependencies += scalaTest   % Test,
    libraryDependencies += mockitoCore % Test,
    libraryDependencies += zio,
    libraryDependencies += zioStreams
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
