package models

import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.user.{User, LoggedInUser}
import models.chat.ChatRoom

trait WithTestChatRoom extends WithTestUser {
  lazy val chatRoom = ChatRoom("hoge-chat", user)
  def save = {
    saveUser
    chatRoom.save
  }
  def clean = {
    chatRooms.deleteWhere(c => c.id <> 0)
    cleanUser
  }
}
