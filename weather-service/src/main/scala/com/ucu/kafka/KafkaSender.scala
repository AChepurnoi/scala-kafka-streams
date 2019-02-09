package com.ucu.kafka

import java.util.{Properties, UUID}

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.Configuration
import com.ucu.kafka.KafkaSender.WeatherMessage
import com.ucu.openweather.api.WeatherResponse
import com.ucu.solar.avro.SolarPanelData
import com.ucu.weather.avro.WeatherInfo
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer


class KafkaSender private(val bootstrapServers: String, val schemaRegistryUrl: String, val topic: String)
  extends Actor with LazyLogging {

  private val props = new Properties() {
    put("bootstrap.servers", bootstrapServers)
    put("schema.registry.url", schemaRegistryUrl)
    put("key.serializer", classOf[StringSerializer].getCanonicalName)
    put("value.serializer", classOf[KafkaAvroSerializer].getCanonicalName)
    put("client.id", UUID.randomUUID().toString)
  }

  private val producer = new KafkaProducer[String, WeatherInfo](props)

  override def receive: Receive = {
    case WeatherMessage(id, weather) =>
      logger.info("Sending data to topic")
      val wInfo = new WeatherInfo(weather.dt, weather.name, weather.main.temp, weather.main.humidity, weather.main.pressure)
      val record = new ProducerRecord[String, WeatherInfo](topic, id, wInfo)
      producer.send(record)
      producer.flush()

    case x => logger.warn(s"Kafka received unknown message: $x")
  }
}

object KafkaSender {

  case class WeatherMessage(id: String, weather: WeatherResponse)

  def props(config: Configuration): Props = Props(
    new KafkaSender(config.kafkaBootstrapServer, config.schemaRegistry, config.weatherTopic))

}