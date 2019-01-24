package com.ucu

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging


case class GetData()

class SolarPanel extends Actor with LazyLogging{

  private val generator: Generator = new RandomGenerator

  override def receive: Receive = {
    case GetData() =>
      sender() ! generator.generate()
      logger.info("Sent data back!")

  }
}
