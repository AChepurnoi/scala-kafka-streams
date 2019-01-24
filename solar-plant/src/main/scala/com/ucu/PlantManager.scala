package com.ucu

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class PlantManager(region: String, messageDestination: ActorRef) extends Actor with LazyLogging {

  import context.dispatcher

  implicit val timeout: Timeout = Timeout(10 seconds)

  val tick = context.system.scheduler.schedule(5 seconds, 5 seconds, self, "get_data")
  val children: Set[ActorRef] = (for (i <- 1 to 10) yield context.actorOf(Props[SolarPanel])).toSet

  override def receive: Receive = {
    case "get_data" =>
      logger.info("Getting data")
      val s = sender()
      Future.sequence(children.map(_ ? GetData()))
        .onComplete {
          case Success(results) =>
            logger.info("Got results from panels")
            s ! results
          case Failure(_) => logger.error("Failed future")
        }
    case any => messageDestination ! any
  }

  override def postStop() = tick.cancel()

}
