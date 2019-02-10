package com.ucu

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.ucu.WeatherApp.system
import com.ucu.WeatherServiceSupervisor.IntervalTrigger
import com.ucu.kafka.KafkaSender
import com.ucu.kafka.KafkaSender.WeatherMessage
import com.ucu.openweather.WeatherActor
import com.ucu.openweather.WeatherActor.LoadWeather
import com.ucu.openweather.api.WeatherResponse
import com.ucu.zookeeper.{PlantRegistry, ZKListenerActor}
import com.ucu.zookeeper.PlantRegistry.{NextPlant, PlantStateChanged}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class WeatherServiceSupervisor(val configuration: Configuration) extends Actor with LazyLogging {

  private implicit val timeout: Timeout = Timeout(20 seconds)
  private implicit val exContext: ExecutionContextExecutor = context.dispatcher

  private val registry = context.actorOf(PlantRegistry.props())
  private val listener = context.actorOf(ZKListenerActor.props(configuration, registry))
  private val weatherActor = context.actorOf(WeatherActor.props(configuration))
  private val kafkaSender = context.actorOf(KafkaSender.props(configuration))

  private val intervalSec: Int = Math.floorDiv(60, Math.floorDiv(60, configuration.clusterSize))
  logger.info(s"Interval: $intervalSec")
  private val intervalTrigger = context.system.scheduler.schedule(intervalSec seconds, intervalSec seconds, self, IntervalTrigger())

  override def receive: Receive = {
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

object WeatherServiceSupervisor {
  case class IntervalTrigger()

}
