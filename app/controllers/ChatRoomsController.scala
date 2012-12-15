package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play20.auth.Auth
import chat.ChatRoomActor
import models._
import models.chat.ChatRoomRepository
import user.{UserRepository, LoggedInUser}

object ChatRoomsController extends Controller with Auth with AuthConfigImpl {

  def index = authorizedAction(LoggedInUser){ user => implicit request =>
    Ok(views.html.chatRooms.index(user))
  }

  /**
   * Display the chat room page.
   */
  def chatRoom = authorizedAction(LoggedInUser){ user => implicit request =>
    Ok(views.html.chatRooms.chatRoom(user))
  }
  
  /**
   * Handles the chat websocket.
   */
  def chat(userId: Long) = WebSocket.async[JsValue] { request  =>
    ChatRoomActor.join(
      chatRoom = ChatRoomRepository.findOrCreate("defaultRoom"),
      userOption = UserRepository.findById(userId)
    )
  }

}
