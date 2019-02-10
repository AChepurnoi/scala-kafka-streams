package com.ucu.panel

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.panel.SolarPanel.GetData


class SolarPanel extends Actor with LazyLogging {

  private val generator: Generator = new RandomGenerator

  override def receive: Receive = {
    case GetData() => sender() ! generator.generate()
  }
}

object SolarPanel {

  case class GetData()
  def props(): Props = Props(new SolarPanel)
}
