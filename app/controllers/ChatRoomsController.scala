package controllers

import play.api._
import libs.concurrent.Promise
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.iteratee._

import models._

import akka.actor._
import akka.util.duration._

import chat.ChatRoomRepository
import org.squeryl.PrimitiveTypeMode._
import jp.t2v.lab.play20.auth.Auth
import user.{UserRepository, LoggedInUser}

object ChatRoomsController extends Controller with Auth with AuthConfigImpl {

  def index = authorizedAction(LoggedInUser){ user => implicit request =>
    Ok(views.html.chatRooms.index())
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
