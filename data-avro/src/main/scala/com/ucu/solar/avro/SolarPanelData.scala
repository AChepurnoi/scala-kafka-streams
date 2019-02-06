/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package com.ucu.solar.avro

import scala.annotation.switch

case class SolarPanelData(var timestamp: Long, var volts: Long, var kWhPerHour: Double, var efficiency: Int) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this(0L, 0L, 0.0, 0)
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
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = SolarPanelData.SCHEMA$
}

object SolarPanelData {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SolarPanelData\",\"namespace\":\"com.ucu.solar.avro\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"volts\",\"type\":\"long\"},{\"name\":\"kWhPerHour\",\"type\":\"double\"},{\"name\":\"efficiency\",\"type\":\"int\"}]}")
}