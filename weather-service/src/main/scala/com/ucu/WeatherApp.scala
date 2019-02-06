package com.ucu

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.openweather.JsonSupport


object WeatherApp extends App with LazyLogging with JsonSupport {


  logger.info("Starting weather service")
  implicit val system: ActorSystem = ActorSystem("weather-system")

  val supervisor: ActorRef = system.actorOf(Props(new WeatherServiceSupervisor()))

}
