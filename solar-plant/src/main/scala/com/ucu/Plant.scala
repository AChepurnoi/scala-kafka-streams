package com.ucu

import java.util
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.scalalogging.{LazyLogging, Logger}
import akka.pattern.ask
import akka.util.Timeout
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import PlantNodeProtocol._
import com.ucu.kafka.KafkaSender


object Plant extends App with LazyLogging {
  implicit val timeout: Timeout = Timeout(10 seconds)
  implicit val ec = ExecutionContext.global

  logger.info("Starting actor system")
  val system = ActorSystem("solar-plant-system")
  val configuration = new Configuration
  val supervisor = system.actorOf(PlantSupervisor.props(configuration))
  logger.info("Actor system started")

  sys.ShutdownHookThread {
    logger.info("Stopping supervisor")
    system.stop(supervisor)
    logger.info("Stopping system")
    system.terminate()
  }

}
