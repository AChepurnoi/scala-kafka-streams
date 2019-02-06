package com.ucu

import java.nio.file.Paths

import akka.actor.Actor
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.cache.{PathChildrenCache, PathChildrenCacheEvent, PathChildrenCacheMode, TreeCache}
import org.apache.curator.retry.ExponentialBackoffRetry

import spray.json._
import PlantNodeProtocol._


abstract class PlantStateChanged

case class PlantRegistered(node: PlantNode) extends PlantStateChanged

case class PlantRemoved(node: PlantNode) extends PlantStateChanged

class ZKListenerActor extends Actor {

  override def preStart(): Unit = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy)
    client.start()
    val cache = new PathChildrenCache(client, "/solar-plant", true)
    cache.start()
    cache.getListenable.addListener((client, event) => {
      event.getType match {
        case PathChildrenCacheEvent.Type.CHILD_ADDED =>
          val nodeJson = Paths.get(event.getData.getPath).getName(1)
          println(s"ZK child added: $nodeJson")
          val plantNode = JsonParser(nodeJson.toString).convertTo[PlantNode]
          context.parent ! PlantRegistered(plantNode)
        case PathChildrenCacheEvent.Type.CHILD_REMOVED =>
          val nodeJson = Paths.get(event.getData.getPath).getName(1)
          println(s"ZK child added: $nodeJson")
          val plantNode = JsonParser(nodeJson.toString).convertTo[PlantNode]
          context.parent ! PlantRemoved(plantNode)
        case _ => println("Unknown event")
      }


    })
  }

  override def receive: Receive = {
    case _ => println("I don't respond")
  }
}


