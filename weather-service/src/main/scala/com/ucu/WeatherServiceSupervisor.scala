package com.ucu

import akka.actor.{Actor, ActorRef, Props}
import com.ucu.openweather.{LoadWeather, WeatherActor, WeatherResponse}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.ucu.WeatherApp.system

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}


case class IntervalTrigger()

class WeatherServiceSupervisor extends Actor with LazyLogging {

  implicit val timeout: Timeout = Timeout(20 seconds)

  private implicit val ex: ExecutionContextExecutor = context.dispatcher
  private val weatherActor = context.system.actorOf(Props(new WeatherActor()))

  private val zkl: ActorRef = context.actorOf(Props[ZKListenerActor])

  private val registry: ActorRef = context.actorOf(Props[PlantRegistry])

  private val kafkaSender = context.actorOf(Props(new KafkaSender()))

  private val intervalTrigger = context.system.scheduler.schedule(1 seconds, 2 seconds, self, IntervalTrigger())


  override def receive: Receive = {
    case "get" => weatherActor ? "get" onComplete {
      case Success(x) => kafkaSender ! x
      case Failure(x) => logger.error(s"[Weather Service]: error: ${x.getMessage}")
    }

    case x: PlantStateChanged =>
      registry ! x

    case IntervalTrigger() =>
      registry ? NextPlant() onComplete {
        case Success(Some(PlantNode(id, lat, lon))) =>
          weatherActor ? LoadWeather(lat, lon) onComplete {
            case Success(weatherResponse@WeatherResponse(_, weather, _, _)) =>
              logger.info(s"Plant $id, Weather $weather")
              kafkaSender ! WeatherMessage(id, weatherResponse)
            case Success(_) => logger.warn("[WeatherSupervisor]: Unknown weather response")
            case Failure(ex) => logger.error(s"[WeatherSupervisor]: ${ex.getMessage}")
          }
        case Success(None) => logger.info("[WeatherSupervisor]: Empty plant registry")
        case Success(_) => logger.warn("[WeatherSupervisor]: Unknown data")
        case Failure(ex) => logger.error(s"$ex")
      }
  }

  override def postStop() = intervalTrigger.cancel()

}
