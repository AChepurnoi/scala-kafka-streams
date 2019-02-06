package com.ucu

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import spray.json._
import PlantNodeProtocol._
import com.ucu.solar.avro.SolarPanelData


class PlantManager(id: String, messageDestination: ActorRef) extends Actor with LazyLogging {

  import context.dispatcher

  implicit val timeout: Timeout = Timeout(10 seconds)

  val tick = context.system.scheduler.schedule(5 seconds, 5 seconds, self, "get_data")


  val children: Set[ActorRef] = (for (i <- 1 to 10) yield context.actorOf(Props[SolarPanel])).toSet


  override def preStart(): Unit = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy)

    val node = PlantNode(id, Math.random() * 75 - 30, Math.random() * 75 - 30)
      client.start()
      client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
        .forPath(s"/solar-plant/${node.toJson}")

  }

  override def receive: Receive = {
    case "get_data" =>
      logger.info("Getting data")
      val s = sender()
      Future.sequence(children.map(_ ? GetData()))
        .mapTo[Set[SolarPanelData]]
        .onComplete {
          case Success(results) =>
            logger.info("Got results from panels")
            s ! Send(id, results)
          case Failure(_) => logger.error("Failed future")
        }
    case any => messageDestination ! any
  }

  override def postStop() = tick.cancel()

}
