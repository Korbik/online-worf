val Http4sVersion = "0.18.0"
val Specs2Version = "4.0.2"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "name.delafargue",
    name := "online-worf",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % "0.9.1",
      "io.circe"        %% "circe-parser"        % "0.9.1",
      "io.circe"        %% "circe-literal"       % "0.9.1",
      "io.warp10"       %  "token"               % "1.2.13",
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    )
  )

resolvers += "cityzendata-bintray" at "http://dl.bintray.com/cityzendata/maven"
resolvers += "hbs-bintray" at "http://dl.bintray.com/hbs/maven"

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)
enablePlugins(JavaAppPackaging)


initialCommands in console := """
import name.delafargue.onlineworf._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import TokenRequest._
"""

