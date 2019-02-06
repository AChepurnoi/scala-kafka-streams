/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package com.ucu.weather.avro

import scala.annotation.switch

case class WeatherInfo(var timestamp: Long, var name: String, var temperature: Double, var humidity: Double, var pressure: Int) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(0L, "", 0.0, 0.0, 0)
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        timestamp
      }.asInstanceOf[AnyRef]
      case 1 => {
        name
      }.asInstanceOf[AnyRef]
      case 2 => {
        temperature
      }.asInstanceOf[AnyRef]
      case 3 => {
        humidity
      }.asInstanceOf[AnyRef]
      case 4 => {
        pressure
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case 0 => this.timestamp = {
        value
      }.asInstanceOf[Long]
      case 1 => this.name = {
        value.toString
      }.asInstanceOf[String]
      case 2 => this.temperature = {
        value
      }.asInstanceOf[Double]
      case 3 => this.humidity = {
        value
      }.asInstanceOf[Double]
      case 4 => this.pressure = {
        value
      }.asInstanceOf[Int]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = WeatherInfo.SCHEMA$
}

object WeatherInfo {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"WeatherInfo\",\"namespace\":\"com.ucu.weather.avro\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"temperature\",\"type\":\"double\"},{\"name\":\"humidity\",\"type\":\"double\"},{\"name\":\"pressure\",\"type\":\"int\"}]}")
}