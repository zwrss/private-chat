
libraryDependencies ++= {
  sys.props.update("submodule", "true")
  Seq()
}

lazy val scalasdk = project.in(file("scala-sdk"))

lazy val server = project.in(file("server")).dependsOn(scalasdk)

lazy val consoleclient = project.in(file("console-client")).dependsOn(scalasdk)
