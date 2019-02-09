package com.ucu

class Configuration{
  val openweatherKey: String = System.getProperty("OW_KEY", "3e59f18e0192bf243c0d708bc2e99a26")
  val clusterSize: Int = System.getProperty("NODES_COUNT", "1").toInt
  val kafkaBootstrapServer: String = System.getProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
  val schemaRegistry: String = System.getProperty("SCHEMA_REGISTRY_URL", "http://localhost:80801")
  val weatherTopic: String = System.getProperty("TOPIC", "weather-data")
  val zookeeper: String = System.getProperty("ZOOKEEPER_URL", "localhost:2181")
}
