package com.ucu.kafka

import java.util.{Properties, UUID}

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.Configuration
import com.ucu.kafka.KafkaSender.Send
import com.ucu.solar.avro.SolarPanelData
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer


class KafkaSender(val kafkaServer: String, val schemaUrl: String, val topic: String) extends Actor with LazyLogging {

  private val props = new Properties() {
    put("bootstrap.servers", kafkaServer)
    put("schema.registry.url", schemaUrl)
    put("key.serializer", classOf[StringSerializer].getCanonicalName)
    put("value.serializer", classOf[KafkaAvroSerializer].getCanonicalName)
    put("client.id", UUID.randomUUID().toString())
  }

  private val producer = new KafkaProducer[String, SolarPanelData](props)

  override def receive: Receive = {
    case Send(id, data) =>
      logger.info("Sending data to topic")
      data.foreach { x =>
        val record = new ProducerRecord[String, SolarPanelData](topic, id, x)
        producer.send(record)
      }
      producer.flush()
  }
}

object KafkaSender {

  case class Send(id: String, data: Set[SolarPanelData])

  def props(configuration: Configuration): Props = Props(
    new KafkaSender(configuration.kafkaBootstrapServer, configuration.schemaRegistry, configuration.solarDataTopic))

}
