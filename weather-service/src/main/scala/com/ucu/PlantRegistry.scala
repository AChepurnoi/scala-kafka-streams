package com.ucu

import java.util

import akka.actor.Actor

import scala.collection.immutable.Queue


case class NextPlant()

case class FailedSync(node: PlantNode)

class PlantRegistry extends Actor {

  override def receive: Receive = onMessage(Map[String, PlantNode](), Vector[PlantNode]())


  private def onMessage(registry: Map[String, PlantNode], vector: Vector[PlantNode]): Receive = {
    case PlantRegistered(x) =>
      context.become(onMessage(registry + (x.id -> x), vector :+ x))
      println(vector :+ x)

    case PlantRemoved(x) =>
      context.become(onMessage(registry - x.id, vector filter (_.id != x.id)))
      println(vector filter (_.id != x.id))

    case NextPlant() =>
      if (vector.nonEmpty) {
        context.become(onMessage(registry, vector.tail :+ vector.head))
        sender() ! Option(vector.head)
      } else {
        sender() ! Option.empty
      }
    case FailedSync(x) =>
      if (registry.keySet contains x.id) {
        context.become(onMessage(registry, x +: vector))
      }
  }

}
