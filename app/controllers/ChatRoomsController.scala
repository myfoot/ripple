package controllers

import play.api._
import libs.concurrent.Promise
import libs.iteratee.{Enumerator, Input, Done}
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play20.auth.Auth
import chat.ChatRoomActor
import models._
import models.chat.ChatRoomRepository
import user.{UserRepository, LoggedInUser}

object ChatRoomsController extends Controller with Auth with AuthConfigImpl {

  def index = authorizedAction(LoggedInUser){ user => implicit request =>
    Ok(views.html.chatRooms.index(user, ChatRoomRepository.all))
  }

  /**
   * Display the chat room page.
   */
  def chatRoom(id:Long) = authorizedAction(LoggedInUser){ user => implicit request =>
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
