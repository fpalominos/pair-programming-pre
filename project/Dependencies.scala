import sbt.*

object Dependencies {
  lazy val catsCore = "org.typelevel" %% "cats-core" % "2.12.0"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.20"
  lazy val mockitoCore = "org.mockito" % "mockito-core" % "3.12.4"
  lazy val zio = "dev.zio" %% "zio" % "2.1.26"
  lazy val zioStreams = "dev.zio" %% "zio-streams" % "2.1.26"
  lazy val zioHttp = "dev.zio" %% "zio-http" % "3.8.1"
  lazy val zioJson = "dev.zio" %% "zio-json" % "0.7.44"
}
