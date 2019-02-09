package com.ucu.openweather.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Coordinates(lon: Double, lat: Double)
case class Weather(temp: Double, humidity: Double, pressure: Int)
case class WeatherResponse(coord: Coordinates, main: Weather, name: String, dt: Long)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val coordinateFormat = jsonFormat2(Coordinates) // contains List[Item]
  implicit val weatherFormat = jsonFormat3(Weather) // contains List[Item]
  implicit val weatherResponseFormat = jsonFormat4(WeatherResponse)
}
