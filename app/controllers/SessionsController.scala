package controllers

import play.api.mvc.{Action, Controller}
import play.api.data._
import play.api.data.Forms._
import models.CoreSchema

import org.squeryl.PrimitiveTypeMode._
import models.user.{User, UserRepository}

object SessionsController extends Controller {

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid user name or password",
      fields => fields match {
        case (username, password) => {
          transaction{
            UserRepository.find(username, password) match {
              case Some(x) => true
              case _ => false
            }
          }
        }
      }
    )
  )

  def index = Action { implicit request =>
    Ok(views.html.sessions.index(loginForm))
  }

  def create = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.sessions.index(errors)),
      value => value match {
        case (username, password) => Redirect(routes.Application.chatRoom(username = Some(username)))
      }
    )
  }

}
