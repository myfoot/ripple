package controllers.chat

import play.api.libs.json.JsValue
import play.api.libs.iteratee.Enumerator

import models.user.User

object ConnectionResult {
  case class Connected(enumerator:Enumerator[JsValue])
  case class CannotConnect(msg: String)
}
