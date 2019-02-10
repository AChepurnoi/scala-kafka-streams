package com.ucu

import java.time.Duration
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import com.ucu.solar.avro.{SolarPanelData, SolarRichData}
import com.ucu.weather.avro.WeatherInfo
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroDeserializerConfig}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.kstream.{JoinWindows, Produced}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Consumed, Joined}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import scala.collection.JavaConverters._
import scala.collection.mutable

object Enricher extends App with LazyLogging {

  logger.info("Starting enricher")
  val configuration = new Configuration

  implicit val avroSerde: GenericAvroSerde = {
    val x = new GenericAvroSerde()
    val param = mutable.Map(
      "schema.registry.url" -> configuration.schemaRegistry,
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> "true").asJava
    x.configure(param, false)
    x
  }

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, configuration.applicationId)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafkaBootstrapServer)
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getCanonicalName)
    p
  }

  val topology = new EnrichTopology
  val streams: KafkaStreams = new KafkaStreams(topology.join(configuration), props)

  streams.cleanUp()
  streams.start()

  sys.ShutdownHookThread {
    streams.close()
  }


}
