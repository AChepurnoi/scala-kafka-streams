package com.ucu

import java.util

class Configuration {
  val env: util.Map[String, String] = System.getenv()
  val kafkaBootstrapServer: String = env.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
  val schemaRegistry: String = env.getOrDefault("SCHEMA_REGISTRY_URL", "http://localhost:8081")
  val weatherTopic: String = env.getOrDefault("WEATHER_TOPIC", "weather-data")
  val solarDataTopic: String = env.getOrDefault("SOLAR_DATA_TOPIC", "solar-panel-data")
  val joinTopic: String = env.getOrDefault("OUTPUT_TOPIC", "solar-rich")
  val applicationId: String = env.getOrDefault("APP_ID", "enrich-service")
  val joinInterval: Long = env.getOrDefault("JOIN_INTERVAL_SEC", "5").toInt

}
