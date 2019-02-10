package com.ucu.zookeeper

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import com.ucu.{Configuration, PlantNode}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import spray.json._
import com.ucu.PlantNodeProtocol._

class ZKNodeActor(val id: String, val zkUrl: String) extends Actor with LazyLogging{

  private val retryPolicy = new ExponentialBackoffRetry(1000, 3)
  private val client = CuratorFrameworkFactory.newClient(zkUrl, retryPolicy)
  private val node = PlantNode(id, Math.random() * 75 - 30, Math.random() * 75 - 30)

  override def preStart(): Unit = {
    client.start()
    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
      .forPath(s"/solar-plant/${node.toJson}")
  }

  override def receive: Receive = {
    case x => logger.warn(s"Unexpected message: $x")
  }

  override def postStop(): Unit = {
    client.close()
  }
}

object ZKNodeActor {
  def props(configuration: Configuration, id: String): Props = Props(new ZKNodeActor(id, configuration.zookeeper))
}