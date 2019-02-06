package com.ucu


import spray.json.DefaultJsonProtocol

case class PlantNode(id: String, lat: Double, lon: Double)

object PlantNodeProtocol extends DefaultJsonProtocol {
  implicit val plantFormat = jsonFormat3(PlantNode)
}