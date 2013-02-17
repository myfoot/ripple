package controllers.chat

import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json._
import play.api.libs.iteratee._
import scala.collection.mutable.{HashSet => MutableSet, Map => MutableMap}
import models.user.User
import controllers.chat.action.ChatAction._
import controllers.chat.ConnectionResult.{Connected, CannotConnect}
import models.chat.ChatRoom
import play.libs.Akka

class ChatRoomActor extends Actor {
  val members = MutableSet.empty[User]

  val (chatEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def receive = {
    case Join(user) => {
      if(members.contains(user)) {
        sender ! CannotConnect("This user is already joined")
      } else {
        members += user
        sender ! Connected(chatEnumerator)
        self ! NotifyJoin(user)
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
    val msg = Json.obj(
      "kind" -> kind,
      "user" -> user.name,
      "message" -> text,
      "members" ->
        members.map(user => Json.toJson(user.name))
    )
    members.foreach{user => println("%s : %s".format(user.name, msg))}
    chatChannel.push(msg)
  }

}

object ChatRoomActor {

  implicit val timeout = Timeout(1 second)

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

  def join(chatRoom: ChatRoom, user: User) = {
    (ChatRoomActor.getActor(chatRoom) ? Join(user)).map {
      case Connected(enumerator) =>
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
