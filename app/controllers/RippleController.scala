package controllers

import play.api.mvc._
import org.squeryl.PrimitiveTypeMode._

trait RippleController extends Controller{
  def withTransaction(f: Request[AnyContent] => Result) = Action { implicit request =>
    transaction{ f(request) }
  }
}
