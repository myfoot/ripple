package controllers.chat

import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.ask
import play.libs.Akka
import play.api.libs.iteratee.Iteratee
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsValue
import play.api.Logger

import controllers.chat.ConnectionResult.Connected
import models.user.{LoggedInUser, User}
import models.chat.action.{Talk, Join}

object Robot {

  def apply(chatRoom: ActorRef) {

    // Create an Iteratee that log all messages to the console.
    val loggerIteratee = Iteratee.foreach[JsValue](event => Logger("robot").info(event.toString))

    val robot = User("Robot", "robo@hoge.com", "", LoggedInUser)

    implicit val timeout = Timeout(1 second)
    // Make the robot join the room
    chatRoom ? (Join(robot)) map {
      case Connected(robotChannel) =>
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