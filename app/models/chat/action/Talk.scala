package models.chat.action

import controllers.chat.action.ChatAction
import models.user.User
import models.chat.ChatRoom
import org.json4s.JsonDSL._
import org.json4s.JsonAST.JValue
//import org.json4s.jackson.JsonMethods._

case class Talk(user: User, message: String) extends ChatAction {
  def key(chatRoom: ChatRoom) = s"chatroom:${chatRoom.id}:talks"
  def toJson: JValue = {
   ("userId" -> user.id) ~ ("message" -> message)
  }
}
