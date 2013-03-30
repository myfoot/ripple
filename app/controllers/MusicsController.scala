package controllers

import models.user.LoggedInUser
import play.api.libs.json.Json
import controllers.util.FilePartExtensions._
import controllers.util.ContentTypes._
import play.api.i18n.Messages
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import models.music.{Music, MusicRepository}
import java.io.File
import models.util.file.Files._
import play.api.mvc.SimpleResult
import models.util.ValidationTypes._

object MusicsController extends NeedAuthController {
  def create = authorizedAction(parse.multipartFormData, LoggedInUser) { user => implicit request =>
    request.body.file("file").map { music =>
      music.filterContentType(m4a,mp3).map{ contentType =>
        insertMusic(
          file = music,
          success = {x => MusicCreateResponceBuilder.success(x) },
          error = {x => MusicCreateResponceBuilder.error("error.music.file.invalidContentType")}
        )
      }.getOrElse {
        MusicCreateResponceBuilder.error("error.music.file.invalidContentType")
      }
    } getOrElse {
      MusicCreateResponceBuilder.error("error.music.file.required")
    }
  }

  private def insertMusic[A](file: FilePart[TemporaryFile], success: Music => SimpleResult[A], error: Error => SimpleResult[A]) = {
    deleteAfter(File.createTempFile("tmp", file.filename)) { tmpFile =>
      file.ref.moveTo(tmpFile, true)
      MusicRepository.insert(Music(tmpFile)) match {
        case Right(music) => success(music)
        case Left(errors) => error(errors)
      }
    }
  }

  private object MusicCreateResponceBuilder {
    val successKey = "success"
    val errorKey = "error"
    def success(file: FilePart[TemporaryFile]) = Ok(Json.obj(successKey -> true, errorKey -> false))
    def error(messageKey: String) = Forbidden(Json.obj(successKey -> false, errorKey -> Messages(messageKey)))
  }
}
