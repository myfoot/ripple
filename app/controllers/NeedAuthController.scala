package controllers

import play.api.mvc._
import jp.t2v.lab.play20.auth.Auth
import org.squeryl.PrimitiveTypeMode._

trait NeedAuthController extends Controller with Auth with AuthConfigImpl {
  override def authorizedAction(authority: Authority)(f: User => Request[AnyContent] => Result): Action[(AnyContent, User)] = {
    super.authorizedAction(authority){ user => request =>
      transaction{ f(user)(request) }
    }
  }

  override def authorizedAction[A](p: BodyParser[A], authority: NeedAuthController#Authority)(f: (NeedAuthController#User) => (Request[A]) => Result): Action[(A, NeedAuthController#User)] = {
    super.authorizedAction(p, authority){ user => request =>
      transaction{ f(user)(request) }
    }
  }
}