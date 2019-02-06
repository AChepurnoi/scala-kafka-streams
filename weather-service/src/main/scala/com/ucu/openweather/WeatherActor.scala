package com.ucu.openweather

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.ucu.openweather.WeatherResponse

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.pattern.{PipeableFuture, pipe}
import com.google.common.util.concurrent.RateLimiter

import scala.util.{Failure, Success, Try}


case class LoadWeather(lat: Double, lon: Double)


class WeatherActor extends Actor with JsonSupport {

  private val key: String = "3e59f18e0192bf243c0d708bc2e99a26"

  private val weatherURI = "https://api.openweathermap.org/data/2.5/weather"
  private implicit val system: ActorSystem = context.system
  private implicit val ex: ExecutionContextExecutor = context.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  //  private val limiter = RateLimiter.create(.5)

  def weather(lat: Double, lon: Double): Future[WeatherResponse] = {
    val request = HttpRequest(uri = Uri(weatherURI)
      .withQuery(Query(
        "lat" -> s"${lat}",
        "lon" -> s"${lon}",
        "appid" -> key,
        "units" -> "metric")))
    Http().singleRequest(request).flatMap(Unmarshal(_).to[WeatherResponse])
  }

  override def receive: Receive = {
    case LoadWeather(lat, lon) => weather(lat, lon) pipeTo sender()
  }

}
