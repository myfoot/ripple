package controllers

import play.api.mvc.{Action, Controller}
import play.api.data._
import play.api.data.Forms._

import models.user._
import jp.t2v.lab.play20.auth.LoginLogout

object SessionsController extends Controller with LoginLogout with AuthConfigImpl {

  val loginForm = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(UserRepository.find)(_.map(user => (user.name, "")))
      .verifying ("Invalid user name or password", result => result.isDefined)
  )

  def index = Action { implicit request =>
    Ok(views.html.sessions.index(loginForm))
  }

  def logout = Action {implicit request =>
    gotoLogoutSucceeded
  }

  def create = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.sessions.index(errors)),
      user => gotoLoginSucceeded(user.get.id)
    )
  }
}
