package controllers.request

import play.api.mvc.RequestHeader

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2012/12/31
 * Time: 1:30
 * To change this template use File | Settings | File Templates.
 */
sealed trait RequestType

object RequestType {
  implicit def convertToXmlHttpRequest(request:RequestHeader) = new RequestTypeJudge(request)

  object HttpRequest extends RequestType
  object XmlHttpRequest extends RequestType
}

class RequestTypeJudge(request:RequestHeader) {
  def requestType: RequestType = {
    request.headers.get("X-Requested-With") match {
      case Some("XMLHttpRequest") => RequestType.XmlHttpRequest
      case _ => RequestType.HttpRequest
    }
  }
}