package com.ucu

import java.util.{Properties, UUID}

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import com.ucu.solar.avro.SolarPanelData
import com.ucu.weather.avro.WeatherInfo
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer


case class Send(id: String, data: Set[SolarPanelData])

class KafkaSender extends Actor with LazyLogging {

  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("schema.registry.url", "http://localhost:8081")
  props.put("key.serializer", classOf[StringSerializer].getCanonicalName)
  props.put("value.serializer", classOf[KafkaAvroSerializer].getCanonicalName)
  props.put("client.id", UUID.randomUUID().toString())
  private val producer = new KafkaProducer[String, SolarPanelData](props)


  override def receive: Receive = {

    case Send(id, data) =>
      logger.info("Sending data to topic")
      data.foreach { x =>
        val record = new ProducerRecord[String, SolarPanelData]("solar-panel-data", id, x)
        producer.send(record)
      }
      producer.flush()
  }
}
