package com.chord.akka.webserver

import akka.actor.Terminated
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.chord.akka.actors.NodeActor
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}


/*
*
* Created by: prajw
* Date: 04-Nov-20
*
*/
object HttpServer extends LazyLogging {

  def setupServer(): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>

      val nodeActor = context.spawn(NodeActor("HTTPServer"), "HTTPServer")
      context.watch(nodeActor)

      val routes = new NodeRoutes(nodeActor)(context.system)
      startHttpServer(routes.lookupRoutes)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "AkkaHttpServer")


  }

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    //TODO remove hardcoding
    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(s"Server online at http://${address.getHostString}:${address.getPort}")
      case Failure(exception) =>
        system.log.error("Failed to bin HTTP endpoint, terminating system. ", exception)
        system.terminate()
    }

  }

   def stopHttpServer(system: ActorSystem[NodeActor]) ={
    system.log.info("Stopping Http Server")
    system.terminate()
  }
}

