package controllers

import models.user.LoggedInUser
import play.api.libs.json.{JsObject, Json}
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
import models.chat.{ChatRoomRepository, ChatRoom}

object MusicsController extends NeedAuthController {
  def create(chatRoomId: Long) = authorizedAction(parse.multipartFormData, LoggedInUser) { user => implicit request =>
    val result: Option[SimpleResult[JsObject]] =
      for {
        chatRoom <- ChatRoomRepository.find(chatRoomId)
        music <- request.body.file("file")
      } yield {
        music.filterContentType(m4a,mp3).map{ contentType =>
          insertMusic(
            chatRoom = chatRoom,
            file = music,
            success = {x => MusicCreateResponceBuilder.success(x) },
            error = {x => MusicCreateResponceBuilder.error("error.music.file.invalidContentType")}
          )
        }.getOrElse {
          MusicCreateResponceBuilder.error("error.music.file.invalidContentType")
        }

      }
    result match {
      case Some(x) => x
      case None => MusicCreateResponceBuilder.error("error.music.invalid.parameter")
    }
  }

  private def insertMusic[A](chatRoom: ChatRoom, file: FilePart[TemporaryFile], success: Music => SimpleResult[A], error: Error => SimpleResult[A]): SimpleResult[A]= {
    deleteAfter(File.createTempFile("tmp", file.filename)) { tmpFile =>
      file.ref.moveTo(tmpFile, true)
      MusicRepository.insert(chatRoom, Music(tmpFile)) match {
        case Right(music) => success(music)
        case Left(errors) => error(errors)
      }
    }
  }

  private object MusicCreateResponceBuilder {
    val successKey = "success"
    val errorKey = "error"
    def success(music: Music):SimpleResult[JsObject] = Ok(Json.obj(successKey -> true, errorKey -> false, "music" -> Json.obj("title" -> music.songTitle)))
    def error(messageKey: String):SimpleResult[JsObject] = Forbidden(Json.obj(successKey -> false, errorKey -> Messages(messageKey)))
  }
}
