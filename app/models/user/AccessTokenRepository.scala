package models.user

import models.CoreSchema._
import org.squeryl._
import PrimitiveTypeMode._
import models.util.ValidatorTypes._
import models.social.Provider


/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2013/02/16
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
object AccessTokenRepository {
  def find(provider: Provider, token: String, secret: String) =
    accessTokens
      .where(reruestToken => reruestToken.providerName === provider.name)
      .where(requestToken => requestToken.token === token)
      .where(requestToken => requestToken.secret === secret)
      .headOption

  def insert(token: AccessToken): Either[Map[Symbol, ErrorNames], AccessToken] = {
    token.validate match {
      case Left(x) => Left(x)
      case result@Right(_)  => {
        accessTokens.insert(token)
        result
      }
    }
  }
}
