name := "ucu-kafka-streams"

version := "0.1"

scalaVersion := "2.12.8"


lazy val root = (project in file("."))
  .settings(
    name := "ucu-kafka-streams"
  )


lazy val dataAvro = (project in file("data-avro"))
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % "1.8.2",
      "io.spray" %% "spray-json" % "1.3.5"
    ),
    avroSpecificScalaSource in Compile := new File("data-avro/src/main/scala/"),
    sourceGenerators in Compile += (avroScalaGenerate in Compile).taskValue,
    name := "data-avro"
  )

lazy val solarPlant = (project in file("solar-plant"))
  .dependsOn(dataAvro)
  .settings(
    mainClass in assembly := Some("com.ucu.Plant"),
    assemblyJarName in assembly := "solar.jar",
    inThisBuild(List(
      organization := "com.ucu",
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      resolvers += "confluent" at "https://packages.confluent.io/maven/"
    )),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.19",
      "org.apache.kafka" % "kafka-streams" % "2.1.0-cp1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.1.0-cp1",
      "org.apache.kafka" % "kafka-clients" % "2.1.0-cp1",
      "io.confluent" % "kafka-streams-avro-serde" % "5.1.0",
      "io.confluent" % "kafka-avro-serializer" % "5.1.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.apache.curator" % "curator-framework" % "4.1.0" exclude("org.apache.zookeeper", "zookeeper"),
      "org.apache.curator" % "curator-recipes" % "4.1.0"
    ),
    name := "solar-plant"
  )

lazy val weatherService = (project in file("weather-service"))
  .dependsOn(dataAvro)
  .settings(
    mainClass in assembly := Some("com.ucu.WeatherApp"),
    assemblyJarName in assembly := "weather.jar",
    inThisBuild(List(
      organization := "com.ucu",
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      resolvers += "confluent" at "https://packages.confluent.io/maven/"
    )),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.19",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "com.typesafe.akka" %% "akka-http" % "10.1.7",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
      "com.typesafe.akka" %% "akka-stream" % "2.5.19",
      "org.apache.curator" % "curator-framework" % "4.1.0" exclude("org.apache.zookeeper", "zookeeper"),
      "org.apache.curator" % "curator-recipes" % "4.1.0",
      "org.apache.kafka" % "kafka-streams" % "2.1.0-cp1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.1.0-cp1",
      "org.apache.kafka" % "kafka-clients" % "2.1.0-cp1",
      "io.confluent" % "kafka-streams-avro-serde" % "5.1.0",
      "io.confluent" % "kafka-avro-serializer" % "5.1.0",
      "com.google.guava" % "guava" % "27.0.1-jre"
    ),
    name := "weather-service"
  )

lazy val enrichService = (project in file("enrich-service"))
  .dependsOn(dataAvro)
  .settings(
    mainClass in assembly := Some("com.ucu.Enricher"),
    assemblyJarName in assembly := "enrich.jar",

    inThisBuild(List(
      organization := "com.ucu",
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      resolvers += "confluent" at "https://packages.confluent.io/maven/"
    )),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.apache.kafka" % "kafka-streams" % "2.1.0-cp1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.1.0-cp1",
      "org.apache.kafka" % "kafka-clients" % "2.1.0-cp1",
      "io.confluent" % "kafka-streams-avro-serde" % "5.1.0",
      "io.confluent" % "kafka-avro-serializer" % "5.1.0"
    ),
    name := "enrich-service"
  )