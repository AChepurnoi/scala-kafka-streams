package com.ucu

import java.util

class Configuration{
  val env: util.Map[String, String] = System.getenv()
  val openweatherKey: String = env.getOrDefault("OW_KEY", "3e59f18e0192bf243c0d708bc2e99a26")
  val clusterSize: Int = env.getOrDefault("NODES_COUNT", "1").toInt
  val kafkaBootstrapServer: String = env.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
  val schemaRegistry: String = env.getOrDefault("SCHEMA_REGISTRY_URL", "http://localhost:8081")
  val weatherTopic: String = env.getOrDefault("TOPIC", "weather-data")
  val zookeeper: String = env.getOrDefault("ZOOKEEPER_URL", "localhost:2181")
}
