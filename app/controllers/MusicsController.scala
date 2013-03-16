package controllers

import models.user.LoggedInUser
import play.api.libs.json.Json

object MusicsController extends NeedAuthController {
  def create = authorizedAction(parse.multipartFormData, LoggedInUser) { user => implicit request =>
    request.body.file("file").map { music =>
      val filename = music.filename
      val contentType = music.contentType.getOrElse("unknown")
      Ok(Json.obj("success" -> true, "error" -> false, "file" -> Json.obj("name" -> filename, "type" -> contentType)))
    } getOrElse {
      Forbidden(Json.obj("success" -> false, "error" -> "file not exist"))
    }
  }
}
