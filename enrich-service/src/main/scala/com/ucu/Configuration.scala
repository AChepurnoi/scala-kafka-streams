package com.ucu

class Configuration {
  val kafkaBootstrapServer: String = System.getProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
  val schemaRegistry: String = System.getProperty("SCHEMA_REGISTRY_URL", "http://localhost:8081")
  val weatherTopic: String = System.getProperty("WEATHER_TOPIC", "weather-data")
  val solarDataTopic: String = System.getProperty("SOLAR_DATA_TOPIC", "solar-panel-data")
  val joinTopic: String = System.getProperty("OUTPUT_TOPIC", "solar-rich")
  val applicationId: String = System.getProperty("APP_ID", "enrich-service")
  val joinInterval: Long = System.getProperty("JOIN_INTERVAL_SEC", "5").toInt

}
