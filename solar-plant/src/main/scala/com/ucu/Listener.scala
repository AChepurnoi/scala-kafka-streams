package com.ucu

import java.util

import akka.util.Timeout
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.framework.api.{BackgroundCallback, CuratorEvent}
import org.apache.curator.framework.recipes.cache.TreeCache
import org.apache.curator.retry.ExponentialBackoffRetry

import scala.concurrent.ExecutionContext

object Listener extends App {
  implicit val ec = ExecutionContext.global


  val retryPolicy = new ExponentialBackoffRetry(1000, 3)
  val client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy)
  client.start()

  private val cache = new TreeCache(client, "/solar-plant")
  cache.start()



  while (true) {
    Thread.sleep(1000)
    val stringToData = cache.getCurrentChildren("/solar-plant")
    println(stringToData.keySet())

  }


}
