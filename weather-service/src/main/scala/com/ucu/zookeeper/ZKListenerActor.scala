package com.ucu.zookeeper

import java.nio.file.{Path, Paths}

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.{Configuration, PlantNode}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.cache._
import org.apache.curator.retry.ExponentialBackoffRetry
import spray.json._
import com.ucu.PlantNodeProtocol._
import com.ucu.zookeeper.PlantRegistry.{PlantRegistered, PlantRemoved}


class ZKListenerActor private(val zookeeperURL: String, val registry: ActorRef) extends Actor with LazyLogging {

  override def preStart(): Unit = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client = CuratorFrameworkFactory.newClient(zookeeperURL, retryPolicy)
    client.start()

    val cache = new PathChildrenCache(client, "/solar-plant", true) {
      getListenable.addListener(handle)
      start()
    }
  }

  private def handle: PathChildrenCacheListener = { (_, event) =>
    event.getType match {
      case PathChildrenCacheEvent.Type.CHILD_ADDED =>
        val plantNode: PlantNode = parseNode(event)
        logger.info(s"ZK node added: $plantNode")
        registry ! PlantRegistered(plantNode)

      case PathChildrenCacheEvent.Type.CHILD_REMOVED =>
        val plantNode: PlantNode = parseNode(event)
        logger.info(s"ZK node removed: $plantNode")
        registry ! PlantRemoved(plantNode)

      case x => logger.info(s"Unhandled event received: $x")
    }
  }

  private def parseNode(event: PathChildrenCacheEvent): PlantNode = {
    val nodeJson = Paths.get(event.getData.getPath).getName(1)
    JsonParser(nodeJson.toString).convertTo[PlantNode]
  }

  override def receive: Receive = {
    case x => logger.warn(s"Message received by ZK listener $x")
  }
}

object ZKListenerActor {

  def props(config: Configuration, registry: ActorRef): Props = Props(
    new ZKListenerActor(config.zookeeper, registry))
}


