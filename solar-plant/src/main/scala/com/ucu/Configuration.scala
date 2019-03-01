package com.ucu

import java.util

class Configuration {
  val env: util.Map[String, String] = System.getenv()
  val kafkaBootstrapServer: String = env.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
  val schemaRegistry: String = env.getOrDefault("SCHEMA_REGISTRY_URL", "http://localhost:8081")
  val solarDataTopic: String = env.getOrDefault("TOPIC", "solar-panel-data")
  val zookeeper: String = env.getOrDefault("ZOOKEEPER_URL", "localhost:2181")
  val panelCount : Int = env.getOrDefault("PANEL_COUNT", "20").toInt
  val intervalSec : Int = env.getOrDefault("INTERVAL_SEC", "5").toInt

}
