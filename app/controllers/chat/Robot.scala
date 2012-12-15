package controllers.chat

import controllers.chat.action.ChatAction._
import play.libs.Akka
import akka.actor.ActorRef
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsValue
import play.api.Logger
import models.user.{LoggedInUser, User}
import controllers.chat.ConnectionResult.Connected

import akka.util
import akka.util.duration._
import akka.pattern.ask

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