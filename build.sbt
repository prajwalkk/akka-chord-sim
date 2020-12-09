lazy val akkaHttpVersion = "10.2.1"
lazy val akkaVersion = "2.6.10"
logLevel := Level.Debug
dockerBaseImage := "openjdk:jre-alpine"
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.cs441.group1",
      scalaVersion := "2.13.3"
    )),
    name := "OverlayNetworkSimulator_Group1",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "com.typesafe" % "config" % "1.2.1",

      // YAML
      "net.jcazevedo" %% "moultingyaml" % "0.4.2"

    )
  )
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)
mainClass in(Compile, run) := Some("com.chord.akka.SimulationDriver")




