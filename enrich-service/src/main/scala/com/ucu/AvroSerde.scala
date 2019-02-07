package com.ucu

import java.util

import com.ucu.Enricher.avroSerde
import com.ucu.solar.avro.SolarPanelData
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}

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
