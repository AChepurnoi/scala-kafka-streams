package com.ucu

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

class KafkaSender extends Actor with LazyLogging{
  override def receive: Receive = {
    case any => logger.info(s"Kafka sender >>> $any")
  }
}
