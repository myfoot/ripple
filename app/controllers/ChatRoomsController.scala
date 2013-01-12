package controllers

import play.api._
import libs.concurrent.Promise
import libs.iteratee.{Enumerator, Input, Done}
import play.api.mvc._
import play.api.libs.json._
import com.codahale.jerkson.Json._
import play.api.libs.json.Writes._
import jp.t2v.lab.play20.auth.Auth

import chat.ChatRoomActor
import request.RequestType
import request.RequestType._
import _root_.models.chat.ChatRoomRepository
import _root_.models.chat.ChatRoom
import _root_.models.user.{UserRepository, LoggedInUser}

import play.api.data._
import play.api.data.Forms._

object ChatRoomsController extends Controller with Auth with AuthConfigImpl {

  def index = authorizedAction(LoggedInUser){ user => implicit request =>
    request.requestType match {
      case RequestType.XmlHttpRequest => Ok(generate(ChatRoomRepository.all.map(_.toMap)))
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
      errors => Ok(Json.toJson(Map("error" -> true, "success" -> false))),
      chatRoom => ChatRoomRepository.insert(chatRoom) match {
        case Right(x) => {
          Ok(generate(Map("success" -> true, "error" -> false, "messages" -> Map.empty)))
        }
        case Left(x) => Ok(generate(Map("success" -> false, "error" -> true, "messages" -> x.map{case (key, value) => (key.name, value)}.toMap)))
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
  def chat(id: Long, userId: Long) = WebSocket.async[JsValue] { request  =>
    (ChatRoomRepository.find(id), UserRepository.findById(userId)) match {
      case (Some(chatRoom), Some(user)) => ChatRoomActor.join(chatRoom, user)
      case _ => {
        val iteratee = Done[JsValue,Unit]((),Input.EOF)
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString("Error Error Error")))).andThen(Enumerator.enumInput(Input.EOF))
        Promise.pure(iteratee,enumerator)
      }
    }
  }

}
