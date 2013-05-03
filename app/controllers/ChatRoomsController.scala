package controllers

import play.api._
import i18n.Messages
import libs.concurrent.Promise
import libs.iteratee.{Enumerator, Input, Done}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Writes._
import org.squeryl.PrimitiveTypeMode._

import chat.ChatRoomActor
import request.RequestType
import request.RequestType._
import _root_.models.chat.ChatRoomRepository
import _root_.models.chat.ChatRoom
import _root_.models.user.{UserRepository, LoggedInUser}

import play.api.data._
import play.api.data.Forms._

object ChatRoomsController extends NeedAuthController {

  // TODO: ControllerのBaseクラスとか作ってそっち持ってく？？
  implicit val chatRoomWrites = new Writes[ChatRoom] {
    def writes(o: ChatRoom): JsValue = {
      Json.obj(
        "id" -> o.id,
        "name" -> o.name
      )
    }
  }

  def index = authorizedAction(LoggedInUser){ user => implicit request =>
    request.requestType match {
      case RequestType.XmlHttpRequest => Ok(JsArray(ChatRoomRepository.all.map(Json.toJson(_))))
      case _ => Ok(views.html.chatRooms.index(user, ChatRoomRepository.all))
    }
  }

  val createForm = Form(
    mapping(
      "name" -> text
    )(ChatRoom.apply)((room:ChatRoom) => Some(room.name))
  )
  /**
   * XHR only
   */
  def create = authorizedAction(LoggedInUser){ user => implicit request =>
    createForm.bindFromRequest.fold(
      // errorはありえないけどね
      errors => Ok(Json.obj("error" -> true, "success" -> false)),
      chatRoom => ChatRoomRepository.insert(chatRoom) match {
        case Right(_) => Ok(Json.obj("success" -> true, "error" -> false, "messages" -> ""))
        case Left(x) => Ok(Json.obj(
          "success" -> false,
          "error" -> true,
          "messages" -> Json.toJson(x.map{case (key, value) =>
            (key.name, Json.toJson(value.map(validationName => Messages("error.chatroom.%s.%s".format(key.name, validationName.name)))))
          }.toMap)
        ))
      }
    )
  }

  /**
   * Display the chat room page.
   */
  def show(id:Long) = authorizedAction(LoggedInUser){ user => implicit request =>
    ChatRoomRepository.find(id) match {
      case Some(chatRoom) => Ok(views.html.chatRooms.chatRoom(chatRoom, user))
      case None => Redirect(routes.ChatRoomsController.index())
    }
  }
  
  /**
   * Handles the chat websocket.
   */
  def chat(id: Long, userId: Long) = WebSocket.async[JsValue] { request  => transaction {
    (ChatRoomRepository.find(id), UserRepository.findById(userId)) match {
      case (Some(chatRoom), Some(user)) => ChatRoomActor.join(chatRoom, user)
      case _ => {
        val iteratee = Done[JsValue,Unit]((),Input.EOF)
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString("Error Error Error")))).andThen(Enumerator.enumInput(Input.EOF))
        Promise.pure(iteratee,enumerator)
      }
    }
  }}

}
