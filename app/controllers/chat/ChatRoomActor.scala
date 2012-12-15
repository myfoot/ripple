package controllers.chat

import akka.actor._
import akka.util.duration._
import akka.util
import akka.pattern.ask
import play.api.libs.concurrent._

import play.api.libs.json._
import play.api.libs.iteratee._
import scala.collection.mutable.{Map => MutableMap}
import models.user.User
import controllers.chat.action.ChatAction._
import controllers.chat.ConnectionResult.{Connected, CannotConnect}
import models.chat.ChatRoom
import play.libs.Akka

class ChatRoomActor extends Actor {

  val members = MutableMap.empty[User, PushEnumerator[JsValue]]

  def receive = {
    case Join(userOption) => {
      userOption.map{user =>
        val channel =  Enumerator.imperative[JsValue]( onStart = self ! NotifyJoin(user))
        if(members.contains(user)) {
          sender ! CannotConnect("This user is already joined")
        } else {
          members += (user -> channel)
          sender ! Connected(user, channel)
        }
      }.getOrElse{
        sender ! CannotConnect("This user is not exist")
      }
    }

    case NotifyJoin(user) => {
      notifyAll("join", user, "has entered the room")
    }

    case Talk(user, text) => {
      notifyAll("talk", user, text)
    }

    case Quit(user) => {
      members -= user
      notifyAll("quit", user, "has leaved the room")
    }
  }

  def notifyAll(kind: String, user: User, text: String) {
    val msg = JsObject(
      Seq(
        "kind" -> JsString(kind),
        "user" -> JsString(user.name),
        "message" -> JsString(text),
        "members" -> JsArray(
          members.keySet.toList.map(user => JsString(user.name))
        )
      )
    )
    members.foreach {
      case (user, channel) => {
        println("%s : %s".format(user.name, msg))
        channel.push(msg)
      }
    }
  }

}

object ChatRoomActor {

  implicit val timeout = util.Timeout(1 second)

  val chatRoomActors = MutableMap.empty[ChatRoom, ActorRef]

  private def default = {
    val roomActor = Akka.system.actorOf(Props[ChatRoomActor])
    // Create a bot user (just for fun)
    Robot(roomActor)
    roomActor
  }

  def getActor(chatRoom:ChatRoom):ActorRef = {
    chatRoomActors.get(chatRoom).getOrElse{
      val actor = default
      chatRoomActors += (chatRoom -> actor)
      actor
    }
  }

  def join(chatRoom: ChatRoom, userOption: Option[User]) = {
    (ChatRoomActor.getActor(chatRoom) ? Join(userOption)).asPromise.map {
      case Connected(user, enumerator) =>
        val iteratee = Iteratee.foreach[JsValue] { event =>
          getActor(chatRoom) ! Talk(user, (event \ "text").as[String])
        }.mapDone { _ =>
          getActor(chatRoom) ! Quit(user)
        }
        (iteratee,enumerator)
      case CannotConnect(error) =>
        val iteratee = Done[JsValue,Unit]((),Input.EOF)
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))
        (iteratee,enumerator)
    }
  }
}
