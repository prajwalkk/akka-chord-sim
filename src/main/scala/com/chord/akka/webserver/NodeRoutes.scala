package com.chord.akka.webserver

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.chord.akka.actors.NodeActorTest.{ActionSuccessful, GetLookupResponse}
import com.chord.akka.actors.{LookupObject, LookupObjects, NodeActorTest, RequestObject}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

/*
*
* Created by: prajw
* Date: 05-Nov-20
*
*/
class NodeRoutes(nodeRegistry: ActorRef[NodeActorTest.Command])(implicit val system: ActorSystem[_]) extends LazyLogging{

  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  val lookupRoutes: Route =
    pathPrefix("chord") {
      concat(
        //case: post/
        pathEnd {
          concat(
            post {
              entity(as[RequestObject]) { requestObject =>
                onSuccess(putValues(requestObject)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        path(Segment) { k =>
          concat(
            get {
              // retrieve single lookup info
              onSuccess(getValue(k)) { response =>
                complete(response.description)
              }
            })
        })
    }







  // all routes
  // lookup - get
  // add - post

  def putValues(requestObject: RequestObject): Future[ActionSuccessful] = {
    logger.info(s"[${nodeRegistry.path.name}] received request put")
    nodeRegistry.ask(NodeActorTest.FindNode(requestObject, _))
  }
  def getValue(k: String): Future[ActionSuccessful] = {
    logger.info(s"[${nodeRegistry.path.name}] received request get")
  nodeRegistry.ask(NodeActorTest.SearchDataNode(k, _))}



//  def findNode(lookupObject: LookupObject): ActorPath ={
//    val key = Helper.getIdentifier(lookupObject.key)
//
//    val nodes  = new ListBuffer[ActorPath]
//    for(path <- NodeGroup.NodeList.sorted){
//      val nodekey =Helper.getIdentifier(path.toString.split("/").toSeq.last)
//
//      if ( nodekey >= key) nodes += path
//    }
//
//    if(nodes.nonEmpty) nodes.head
//    else NodeGroup.NodeList.sorted.toSeq(0)
//  }
}
