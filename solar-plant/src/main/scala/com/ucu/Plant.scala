package com.ucu

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.scalalogging.{LazyLogging, Logger}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


object Plant extends App with LazyLogging {
  implicit val timeout: Timeout = Timeout(10 seconds)
  implicit val ec = ExecutionContext.global

  logger.info("Starting actor system")
  val system = ActorSystem("solar-plant-system")
  val kafkaSender = system.actorOf(Props[KafkaSender])
  val manager = system.actorOf(Props(new PlantManager("UA", kafkaSender)))
  logger.info("Actor system started")

}
