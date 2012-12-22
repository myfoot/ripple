package controllers

import models.user._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.Json
import jp.t2v.lab.play20.auth
import auth.AuthConfig

trait AuthConfigImpl extends AuthConfig {
  type Id = Long
  type User = models.user.User

  type Authority = Role

  val idManifest: ClassManifest[Id] = classManifest[Id]

  val sessionTimeoutInSeconds:Int = 60*60*3 // 3h

  def resolveUser(id:Id):Option[User] = UserRepository.findById(id)

  val redirectLogin = Redirect(routes.SessionsController.index())
  def loginSucceeded[A](request:Request[A]):PlainResult = Redirect(routes.ChatRoomsController.index())
  def logoutSucceeded[A](request:Request[A]):PlainResult = redirectLogin
  def authenticationFailed[A](request:Request[A]):PlainResult = redirectLogin
  def authorizationFailed[A](request:Request[A]):PlainResult = Forbidden("no permission")

  def authorize(user: User, authority: Authority): Boolean =
    (user.role, authority) match {
      case (Administrator, _) => true
      case (LoggedInUser, LoggedInUser)|(LoggedInUser, GuestUser) => true
      case (GuestUser, GuestUser) => true
      case _ => false
    }
}
