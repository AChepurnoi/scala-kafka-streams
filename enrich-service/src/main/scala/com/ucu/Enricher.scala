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


  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "enricher-service")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getCanonicalName)
    p
  }

  implicit val avroSerde: GenericAvroSerde = {
    val x = new GenericAvroSerde()
    x.configure(mutable.Map("schema.registry.url" -> "http://localhost:8081",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> "true").asJava, false)
    x
  }

  def combine(solar: SolarPanelData, weather: WeatherInfo): SolarRichData =
    new SolarRichData(solar.timestamp, solar.volts, solar.kWhPerHour, solar.efficiency,
      weather.name, weather.temperature, weather.humidity, weather.pressure)


  implicit val solarPanelDataConsumed: Consumed[String, SolarPanelData] = Consumed.`with`(Serdes.String(), AvroSerde.forClass[SolarPanelData])
  implicit val weatherInfoConsumed: Consumed[String, WeatherInfo] = Consumed.`with`(Serdes.String(), AvroSerde.forClass[WeatherInfo])
  implicit val produced = Produced.`with`(Serdes.String(), AvroSerde.forClass[SolarRichData])


  implicit val joined: Joined[String, SolarPanelData, WeatherInfo] = Joined.`with`(Serdes.String(),
    AvroSerde.forClass[SolarPanelData],
    AvroSerde.forClass[WeatherInfo])


  val builder: StreamsBuilder = new StreamsBuilder
  val weatherStream = builder.stream[String, WeatherInfo]("weather-data")

  builder.stream[String, SolarPanelData]("solar-panel-data")
    .join(weatherStream)(combine, JoinWindows.of(Duration.ofMinutes(1)))
    .to("solar-rich")

  val result: Topology = builder.build()

  val streams: KafkaStreams = new KafkaStreams(result, props)
  streams.cleanUp()
  streams.start()

  sys.ShutdownHookThread {
    streams.close()
  }


}
