organization := "com.github.zwrss"

name := "private-chat-client"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.1",
  "org.yaml" % "snakeyaml" % "1.27",
  "org.fusesource.jansi" % "jansi" % "2.0.1"
)

libraryDependencies ++= {
  if (sys.props.get("submodule") contains "true") Seq()
  else Seq("com.github.zwrss" %% "private-chat-scala-sdk" % "0.1")
}

enablePlugins(JavaAppPackaging)