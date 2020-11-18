package com.chord.akka.actors


import java.net.{URLDecoder, URLEncoder}

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import com.chord.akka.actors.UserActor.Command
import com.chord.akka.utils.SystemConstants





object UserActor {

  var userList = new Array[String](SystemConstants.num_users)
  def apply(id: String): Behavior[Command] =
    Behaviors.setup(context => new UserActor(context, id))

sealed trait Command
final case class lookup_data(key:String,replyTo:ActorRef[RequestSent]) extends Command
final case class put_data(key:String,value:String ,replyTo:ActorRef[RequestSent]) extends Command
case class RequestSent() extends Command

}

class UserActor(context: ActorContext[Command], id: String) extends AbstractBehavior[Command](context) {

  import UserActor._

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case lookup_data(key,replyTo) =>{

        val key1=URLEncoder.encode(key,"UTF-8")

        //Create a post request here
        val req =HttpRequest(
          method = HttpMethods.GET,
          uri = s"http://127.0.0.1:8080/chord/$key1"

        )
        Http()(context.system).singleRequest(req)
        replyTo ! RequestSent()
        Behaviors.same
      }
      case put_data(key,value,replyTo)=>{

        val req =HttpRequest(
          method = HttpMethods.POST,
          uri = s"http://127.0.0.1:8080/chord",
          entity = HttpEntity(ContentTypes.`application/json` ,s"""{"key":"$key","value":"$value"}""")
        )

        Http()(context.system).singleRequest(req)
        replyTo ! RequestSent()


        Behaviors.same
      }


    }
}

