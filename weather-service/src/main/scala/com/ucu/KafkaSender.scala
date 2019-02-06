package com.ucu

import java.util.{Properties, UUID}

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ucu.openweather.WeatherResponse
import com.ucu.solar.avro.SolarPanelData
import com.ucu.weather.avro.WeatherInfo
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer


case class WeatherMessage(id: String, weather: WeatherResponse)

class KafkaSender extends Actor with LazyLogging{


  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("schema.registry.url", "http://localhost:8081")
  props.put("key.serializer", classOf[StringSerializer].getCanonicalName)
  props.put("value.serializer", classOf[KafkaAvroSerializer].getCanonicalName)
  props.put("client.id", UUID.randomUUID().toString())
  private val producer = new KafkaProducer[String, WeatherInfo](props)

  override def receive: Receive = {

    case WeatherMessage(id, weather) =>
      logger.info("Sending data to topic")
      val wInfo = new WeatherInfo(weather.dt, weather.name, weather.main.temp, weather.main.humidity, weather.main.pressure)
      val record = new ProducerRecord[String, WeatherInfo]("weather-data", id, wInfo)
      producer.send(record)
      producer.flush()


    case any => logger.info(s"Kafka sender >>> $any")
  }
}