package com.ucu


case class PanelData(temperature: Int)

trait Generator {
  def generate(): PanelData
}

class RandomGenerator extends Generator {
  override def generate(): PanelData = PanelData((Math.random() * 100).round.toInt)
}
