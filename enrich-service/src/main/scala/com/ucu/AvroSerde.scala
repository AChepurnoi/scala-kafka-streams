package com.ucu

import java.util

import com.ucu.Enricher.avroSerde
import com.ucu.solar.avro.SolarPanelData
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}

/**
  * AvroSerde is an extension of confluent GenericAvroSerde.
  * The purpose of this class to provide typed case class instead of Avro GenericRecord.
  *
  * Schema registry must be parametrized with SPECIFIC_AVRO_READER_CONFIG=true to allow reading SpecificAvroRecords
  * from kafka stream, instead of GenericRecord. (Yes this class is just casting SpecificRecord ¯\_(ツ)_/¯)
  */
object AvroSerde {

  class AvroDeserializer[T <: GenericRecord](val genDes: Deserializer[GenericRecord]) extends Deserializer[T] {

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = genDes.configure(configs, isKey)

    override def deserialize(topic: String, data: Array[Byte]): T = genDes.deserialize(topic, data).asInstanceOf[T]

    override def close(): Unit = genDes.close()
  }

  class AvroSerializer[T <: GenericRecord](val genSer: Serializer[GenericRecord]) extends Serializer[T] {

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = genSer.configure(configs, isKey)

    override def serialize(topic: String, data: T): Array[Byte] = genSer.serialize(topic, data)

    override def close(): Unit = genSer.close()
  }

  def forClass[T <: GenericRecord](implicit avroSerde: GenericAvroSerde): Serde[T] = Serdes.serdeFrom(
    new AvroSerializer[T](avroSerde.serializer()),
    new AvroDeserializer[T](avroSerde.deserializer()))

}
