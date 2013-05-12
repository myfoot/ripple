package models.chat.action

import controllers.chat.action.ChatAction
import models.user.User

case class Talk(user: User, text: String) extends ChatAction
