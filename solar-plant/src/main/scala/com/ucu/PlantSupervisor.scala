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
import com.ucu.Plant.system
import com.ucu.PlantSupervisor.IntervalTick
import com.ucu.kafka.KafkaSender
import com.ucu.kafka.KafkaSender.Send
import com.ucu.panel.SolarPanel
import com.ucu.panel.SolarPanel.GetData
import com.ucu.solar.avro.SolarPanelData
import com.ucu.zookeeper.ZKNodeActor


class PlantSupervisor(id: String, configuration: Configuration) extends Actor with LazyLogging {

  import context.dispatcher

  implicit val timeout: Timeout = Timeout(10 seconds)
  private val scheduler = context.system.scheduler.schedule(5 seconds, 5 seconds, self, IntervalTick())
  private val panels: Set[ActorRef] = (for (_ <- 1 to 10) yield context.actorOf(SolarPanel.props())).toSet
  private val zkNode = context.actorOf(ZKNodeActor.props(configuration, id))
  private val kafkaSender = context.actorOf(KafkaSender.props(configuration))

  override def receive: Receive = {
    case IntervalTick() => Future.sequence(panels.map(_ ? GetData()))
      .mapTo[Set[SolarPanelData]]
      .onComplete {
        case Success(results) => kafkaSender ! Send(id, results)
        case Failure(_) => logger.error("Failed future")
      }
    case x => logger.warn(s"Unknown message in supervisor: $x")
  }

  override def postStop() = scheduler.cancel()

}

object PlantSupervisor {
  case class IntervalTick()

  def props(configuration: Configuration): Props = Props(new PlantSupervisor(UUID.randomUUID().toString, configuration))

}
