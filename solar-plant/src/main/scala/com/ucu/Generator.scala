package com.ucu

import java.time.{Instant, LocalDateTime}

import com.ucu.solar.avro.SolarPanelData


trait Generator {
  def generate(): SolarPanelData
}

class RandomGenerator extends Generator {
  override def generate(): SolarPanelData = SolarPanelData(
    Instant.now().toEpochMilli,
    (Math.random() * 20).toInt,
    Math.random() * 50,
    20 + (Math.random() * 20).toInt)

}
