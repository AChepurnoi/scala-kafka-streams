package com.ucu

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging


object WeatherApp extends App with LazyLogging {
  logger.info("Starting weather service")
  val system: ActorSystem = ActorSystem("weather-system")
  val configuration = new Configuration()
  val supervisor: ActorRef = system.actorOf(Props(new WeatherServiceSupervisor(configuration)))

}
