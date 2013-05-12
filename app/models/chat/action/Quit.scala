package models.chat.action

import models.user.User
import controllers.chat.action.ChatAction

case class Quit(user: User) extends ChatAction
