package controllers

import akka.actor._
import akka.util.duration._

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current
import akka.util
import scala.collection.mutable.{Map => MutableMap}
import models.chat.ChatRoom
import models.user.{LoggedInUser, User}

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

  def default = {
    val roomActor = Akka.system.actorOf(Props[ChatRoomActor])
    // Create a bot user (just for fun)
    Robot(roomActor)
    roomActor
  }

  private def getActor(chatRoom:ChatRoom):ActorRef = {
    chatRoomActors.get(chatRoom).getOrElse{
      val actor = default
      chatRoomActors += (chatRoom -> actor)
      actor
    }
  }

  def join(chatRoom:ChatRoom, userOption:Option[User]):Promise[(Iteratee[JsValue,_],Enumerator[JsValue])] = {
    (getActor(chatRoom) ? Join(userOption)).asPromise.map {
      case Connected(user, enumerator) =>
        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[JsValue] { event =>
          println(getActor(chatRoom))
          getActor(chatRoom) ! Talk(user, (event \ "text").as[String])
        }.mapDone { _ =>
          getActor(chatRoom) ! Quit(user)
        }
        (iteratee,enumerator)

      case CannotConnect(error) =>
        // Connection error
        // A finished Iteratee sending EOF
        val iteratee = Done[JsValue,Unit]((),Input.EOF)
        // Send an error and close the socket
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))
        (iteratee,enumerator)
    }
  }

}

case class Join(user:Option[User])
case class Quit(user:User)
case class Talk(user:User, text: String)
case class NotifyJoin(user:User)

case class Connected(user:User, enumerator:Enumerator[JsValue])
case class CannotConnect(msg: String)


object Robot {

  def apply(chatRoom: ActorRef) {

    // Create an Iteratee that log all messages to the console.
    val loggerIteratee = Iteratee.foreach[JsValue](event => Logger("robot").info(event.toString))

    val robot = User("Robot", "robo@hoge.com", "", LoggedInUser)

    implicit val timeout = util.Timeout(1 second)
    // Make the robot join the room
    chatRoom ? (Join(Some(robot))) map {
      case Connected(robot, robotChannel) =>
        // Apply this Enumerator on the logger.
        robotChannel |>> loggerIteratee
    }

    // Make the robot talk every 30 seconds
    Akka.system.scheduler.schedule(
      30 seconds,
      30 seconds,
      chatRoom,
      Talk(robot, "I'm still alive")
    )
  }

}