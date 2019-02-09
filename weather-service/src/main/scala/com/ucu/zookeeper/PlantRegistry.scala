package com.ucu.zookeeper

import java.util

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.PlantNode
import com.ucu.zookeeper.PlantRegistry.{Enqueue, NextPlant, PlantRegistered, PlantRemoved}

import scala.collection.immutable.Queue


class PlantRegistry extends Actor with LazyLogging {

  override def receive: Receive = onMessage(Map[String, PlantNode](), Vector[PlantNode]())

  private def onMessage(registry: Map[String, PlantNode], vector: Vector[PlantNode]): Receive = {
    case PlantRegistered(x) =>
      val queue = vector :+ x
      context.become(onMessage(registry + (x.id -> x), queue))
      logger.info(s"Registry queue: $queue")

    case PlantRemoved(x) =>
      val queue = vector filter (_.id != x.id)
      context.become(onMessage(registry - x.id, queue))
      logger.info(s"Registry queue: $queue")

    case NextPlant() =>
      if (vector.nonEmpty) {
        context.become(onMessage(registry, vector.tail :+ vector.head))
        sender() ! Option(vector.head)
      } else {
        sender() ! Option.empty
      }

    case Enqueue(x) =>
      if (registry.keySet contains x.id) {
        context.become(onMessage(registry, x +: vector))
      }

  }
}

object PlantRegistry {

  case class NextPlant()

  case class Enqueue(node: PlantNode)

  abstract class PlantStateChanged

  case class PlantRegistered(node: PlantNode) extends PlantStateChanged

  case class PlantRemoved(node: PlantNode) extends PlantStateChanged

  def props(): Props = Props(new PlantRegistry)

}
