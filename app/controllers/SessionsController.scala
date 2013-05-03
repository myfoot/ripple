package controllers

import play.api.mvc.{Action, Controller}
import play.api.data._
import play.api.data.Forms._

import models.user._
import jp.t2v.lab.play20.auth.LoginLogout

object SessionsController extends RippleController with LoginLogout with AuthConfigImpl {

  val loginForm = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(UserRepository.find)(_.map(user => (user.name, "")))
      .verifying ("Invalid user name or password", result => result.isDefined)
  )

  def index = withTransaction{ implicit request =>
    Ok(views.html.sessions.index(loginForm))
  }

  def logout = withTransaction{implicit request =>
    gotoLogoutSucceeded
  }

  def create = withTransaction{ implicit request =>
    loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.sessions.index(errors)),
      user => gotoLoginSucceeded(user.get.id)
    )
  }
}
