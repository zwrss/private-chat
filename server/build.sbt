organization := "com.github.zwrss"

name := "private-chat-server"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.1"
)

enablePlugins(JavaAppPackaging)