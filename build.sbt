name := "ucu-kafka-streams"

version := "0.1"

scalaVersion := "2.12.8"

lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )

lazy val solarPlant = (project in file("solar-plant"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.19",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2")
  )