package controllers

import java.io.File
import play.api.libs.json.{JsObject, Json}
import play.api.i18n.Messages
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import play.api.mvc.SimpleResult
import org.squeryl.PrimitiveTypeMode._
import models.user.LoggedInUser
import models.music.{Music, MusicRepository}
import models.util.file.Files._
import models.util.ValidationTypes._
import models.chat.{ChatRoomRepository, ChatRoom}
import controllers.util.FilePartExtensions._
import controllers.util.ContentTypes._

object MusicsController extends NeedAuthController {
  private val defaultResponseBuilder = new JsonResponseBuilder {}

  def index(chatRoomId: Long) = authorizedAction(LoggedInUser) { user => implicit request => transaction {
    ChatRoomRepository.find(chatRoomId).map{ chat =>
      defaultResponseBuilder.success(
        Json.obj("musics" -> chat.musicsWithoutRawData.map {
          case (id, name, artistName, albumName, songTitle) =>
            Json.obj(
              "id" -> id,
              "fileName" -> name,
              "artistName" -> artistName,
              "albumName" -> albumName,
              "songTitle" -> songTitle
            )
        })
      )
    }.getOrElse {
      defaultResponseBuilder.error("error.chatroom.id.notExist")
    }
  }}

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
            success = {x => MusicCreateResponseBuilder.success(x) },
            error = {x => MusicCreateResponseBuilder.error("error.music.file.invalidContentType")}
          )
        }.getOrElse {
          MusicCreateResponseBuilder.error("error.music.file.invalidContentType")
        }
      }
    result match {
      case Some(x) => x
      case None => MusicCreateResponseBuilder.error("error.music.invalid.parameter")
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

  private object MusicCreateResponseBuilder extends JsonResponseBuilder {
    def success(music: Music):SimpleResult[JsObject] = super.success(Json.obj("music" -> Json.obj("title" -> music.songTitle)))
  }

  private trait JsonResponseBuilder {
    val successKey = "success"
    val errorKey = "error"
    def success(result: JsObject):SimpleResult[JsObject] = Ok(Json.obj(successKey -> true, errorKey -> false) ++ result)
    def error(errorMessageKeys: String*):SimpleResult[JsObject] = Forbidden(Json.obj(successKey -> false, errorKey -> errorMessageKeys.map(Messages(_))))
  }
}
