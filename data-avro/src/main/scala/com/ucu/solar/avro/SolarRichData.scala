/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package com.ucu.solar.avro

import scala.annotation.switch

case class SolarRichData(var timestamp: Long, var volts: Long, var kWhPerHour: Double, var efficiency: Int, var name: String, var temperature: Double, var humidity: Double, var pressure: Int) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(0L, 0L, 0.0, 0, "", 0.0, 0.0, 0)
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case 0 => {
        timestamp
      }.asInstanceOf[AnyRef]
      case 1 => {
        volts
      }.asInstanceOf[AnyRef]
      case 2 => {
        kWhPerHour
      }.asInstanceOf[AnyRef]
      case 3 => {
        efficiency
      }.asInstanceOf[AnyRef]
      case 4 => {
        name
      }.asInstanceOf[AnyRef]
      case 5 => {
        temperature
      }.asInstanceOf[AnyRef]
      case 6 => {
        humidity
      }.asInstanceOf[AnyRef]
      case 7 => {
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
      case 1 => this.volts = {
        value
      }.asInstanceOf[Long]
      case 2 => this.kWhPerHour = {
        value
      }.asInstanceOf[Double]
      case 3 => this.efficiency = {
        value
      }.asInstanceOf[Int]
      case 4 => this.name = {
        value.toString
      }.asInstanceOf[String]
      case 5 => this.temperature = {
        value
      }.asInstanceOf[Double]
      case 6 => this.humidity = {
        value
      }.asInstanceOf[Double]
      case 7 => this.pressure = {
        value
      }.asInstanceOf[Int]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = SolarRichData.SCHEMA$
}

object SolarRichData {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SolarRichData\",\"namespace\":\"com.ucu.solar.avro\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"volts\",\"type\":\"long\"},{\"name\":\"kWhPerHour\",\"type\":\"double\"},{\"name\":\"efficiency\",\"type\":\"int\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"temperature\",\"type\":\"double\"},{\"name\":\"humidity\",\"type\":\"double\"},{\"name\":\"pressure\",\"type\":\"int\"}]}")
}