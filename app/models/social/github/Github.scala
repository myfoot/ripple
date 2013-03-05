package models.social.github

import models.social._
import models.social.ConsumerToken
import models.social.Token

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2013/02/18
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
object GitHub extends Provider{
  val provider: Provider = this
  val name: String = "github"
  val consumerToken = ConsumerToken("", "")
  def socialUser(token: Token): SocialUser = InvalidUser
}
