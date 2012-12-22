package controllers.chat.action

import models.user.User

object ChatAction {
  case class Join(user:User)
  case class Quit(user:User)
  case class Talk(user:User, text: String)
  case class NotifyJoin(user:User)
}
