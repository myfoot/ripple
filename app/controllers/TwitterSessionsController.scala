package controllers

import play.api.mvc.{Action, RequestHeader, Controller}
import jp.t2v.lab.play20.auth.LoginLogout
import models.social.Token
import models.social.twitter.Twitter
import models.user._
import play.api.libs.oauth.ServiceInfo
import play.api.libs.oauth.OAuth
import play.api.libs.oauth.RequestToken
import play.api.libs.oauth.ConsumerKey
import scala.Some
import java.lang.IllegalStateException

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2013/02/16
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
object TwitterSessionsController extends RippleController with LoginLogout with AuthConfigImpl {

  val oauthClient = OAuth(
    ServiceInfo(
      Twitter.requestTokenUrl,
      Twitter.accessTokenUrl,
      Twitter.authorizationUrl,
      ConsumerKey(Twitter.consumerToken.token, Twitter.consumerToken.secret)
    ),
    false)

  val tokenKey = "token-twitter"
  val secretKey = "secret-twitter"

  def authenticate = withTransaction{ implicit request =>
    request.getQueryString("oauth_verifier").map { verifier =>
      handleAccessToken(verifier)
    }.getOrElse {
      handleRequestToken
    }
  }

  private def handleAccessToken(verifier: String)(implicit request: RequestHeader) = {
    sessionTokenPair(request) match {
      case Some(requestToken) => {
        oauthClient.retrieveAccessToken(requestToken, verifier) match {
          case Right(accessToken) => {
            removeSessionTokenPair
            rippleUser(accessToken).map{user =>
              gotoLoginSucceeded(user.id)
            }.getOrElse {
              println("not found user")
              redirectLogin
            }
          }
          case Left(e) => throw e
        }
      }
      case _ => throw new IllegalStateException("does not exist token&secret in session")
    }
  }

  private def handleRequestToken(implicit request: RequestHeader) = {
    oauthClient.retrieveRequestToken(routes.TwitterSessionsController.authenticate.absoluteURL(false)) match {
      case Right(requestToken) => {
        Redirect(oauthClient.redirectUrl(requestToken.token)).withSession(tokenKey -> requestToken.token, secretKey -> requestToken.secret)
      }
      case Left(e) => throw e
    }
  }

  private def sessionTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get(tokenKey)
      secret <- request.session.get(secretKey)
    } yield {
      RequestToken(token, secret)
    }
  }

  private def removeSessionTokenPair(implicit request: RequestHeader) {
    request.session - tokenKey
    request.session - secretKey
  }

  private def rippleUser(accessToken: RequestToken): Option[models.user.User] = {
    AccessTokenRepository.find(Twitter, accessToken.token, accessToken.secret) match {
      case Some(registeredToken) => registeredToken.user
      case None =>
        val socialUser = Twitter.socialUser(Token(accessToken.token, accessToken.secret))
        UserRepository.insertAsSocialUser(socialUser, accessToken.token, accessToken.secret) match {
          case Right((user, token)) => Some(user)
          case Left(_) => None
        }
    }
  }
}
