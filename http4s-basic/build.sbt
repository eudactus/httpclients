name := "http4s-basic"

organization := "com.example"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.http4s"                  %% "http4s-dsl"                  % "0.10.0",
  "org.http4s"                  %% "http4s-client"               % "0.10.0",
  "org.http4s"                  %% "http4s-blaze-client"         % "0.10.0"
)