package com.ucu.openweather

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import com.ucu.Configuration
import com.ucu.openweather.WeatherActor.LoadWeather
import com.ucu.openweather.api.{JsonSupport, WeatherResponse}

import scala.concurrent.{ExecutionContextExecutor, Future}


class WeatherActor private(val key: String) extends Actor with JsonSupport with LazyLogging{

  private implicit val system: ActorSystem = context.system
  //  @TODO replace with different thread pool
  private implicit val ex: ExecutionContextExecutor = context.dispatcher
  private implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  def weather(lat: Double, lon: Double): Future[WeatherResponse] = {
    val request = HttpRequest(uri = Uri(WeatherActor.openweatherURI)
      .withQuery(Query(
        "lat" -> s"$lat",
        "lon" -> s"$lon",
        "appid" -> key,
        "units" -> "metric")))
    Http().singleRequest(request).flatMap(Unmarshal(_).to[WeatherResponse])
  }

  override def receive: Receive = {
    case LoadWeather(lat, lon) => weather(lat, lon) pipeTo sender()

    case x => logger.warn(s"Weather actor received unknown message: $x")
  }

}

object WeatherActor {
  private val openweatherURI = "https://api.openweathermap.org/data/2.5/weather"

  case class LoadWeather(lat: Double, lon: Double)

  def props(config: Configuration): Props = Props(new WeatherActor(config.openweatherKey))

}