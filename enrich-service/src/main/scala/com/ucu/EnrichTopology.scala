package com.ucu

import java.time.Duration

import com.ucu.solar.avro.{SolarPanelData, SolarRichData}
import com.ucu.weather.avro.WeatherInfo
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.{JoinWindows, Produced}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Consumed, Joined}

class EnrichTopology(implicit avroSerde: GenericAvroSerde) {

  private implicit val solarPanelDataConsumed: Consumed[String, SolarPanelData] =
    Consumed.`with`(Serdes.String(), AvroSerde.forClass[SolarPanelData])

  private implicit val weatherInfoConsumed: Consumed[String, WeatherInfo] =
    Consumed.`with`(Serdes.String(), AvroSerde.forClass[WeatherInfo])

  private implicit val produced: Produced[String, SolarRichData] =
    Produced.`with`(Serdes.String(), AvroSerde.forClass[SolarRichData])

  private implicit val joined: Joined[String, SolarPanelData, WeatherInfo] =
    Joined.`with`(Serdes.String(), AvroSerde.forClass[SolarPanelData], AvroSerde.forClass[WeatherInfo])


  private def combine(solar: SolarPanelData, weather: WeatherInfo): SolarRichData =
    new SolarRichData(solar.timestamp, solar.volts, solar.kWhPerHour, solar.efficiency,
      weather.name, weather.temperature, weather.humidity, weather.pressure)

  def join(configuration: Configuration) = {
    val builder: StreamsBuilder = new StreamsBuilder
    val weatherStream = builder.table[String, WeatherInfo](configuration.weatherTopic)
    builder.stream[String, SolarPanelData](configuration.solarDataTopic)
      .join(weatherStream)(combine)
      .to(configuration.joinTopic)

    builder.build()
  }

}
