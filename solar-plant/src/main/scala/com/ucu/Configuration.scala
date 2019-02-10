package com.ucu

class Configuration {
  val kafkaBootstrapServer: String = System.getProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
  val schemaRegistry: String = System.getProperty("SCHEMA_REGISTRY_URL", "http://localhost:8081")
  val solarDataTopic: String = System.getProperty("TOPIC", "solar-panel-data")
  val zookeeper: String = System.getProperty("ZOOKEEPER_URL", "localhost:2181")
  val panelCount : Int = System.getProperty("PANEL_COUNT", "20").toInt
  val intervalSec : Int = System.getProperty("INTERVAL_SEC", "5").toInt

}
