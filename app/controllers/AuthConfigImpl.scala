package controllers

import models.user._
import play.api.mvc._
import play.api.mvc.Results._
import jp.t2v.lab.play20.auth
import auth.AuthConfig
import reflect.ClassTag
import scala.reflect.classTag
import org.squeryl.PrimitiveTypeMode._

trait AuthConfigImpl extends AuthConfig {
  type Id = Long
  type User = models.user.User
  type Authority = Role

  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds:Int = 60*60*3 // 3h


  def resolveUser(id:Id):Option[User] = transaction{ UserRepository.findById(id) }

  val redirectLogin = Redirect(routes.SessionsController.index())
  def loginSucceeded(request: RequestHeader): Result = Redirect(routes.ChatRoomsController.index())
  def logoutSucceeded(request: RequestHeader): Result = redirectLogin
  def authenticationFailed(request: RequestHeader): Result = redirectLogin
  def authorizationFailed(request: RequestHeader): Result = Forbidden("no permission")

  def authorize(user: User, authority: Authority): Boolean =
    (user.role, authority) match {
      case (Administrator, _) => true
      case (LoggedInUser, LoggedInUser)|(LoggedInUser, GuestUser) => true
      case (GuestUser, GuestUser) => true
      case _ => false
    }
}
