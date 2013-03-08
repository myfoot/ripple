package models.social

import facebook.Facebook
import github.GitHub
import google.Google
import twitter.Twitter

trait Provider extends ProviderConfig {
  val name:String
  val consumerToken: ConsumerToken
  def socialUser(token: Token): SocialUser
}

object Provider {
  private lazy val providers = Seq(Twitter, GitHub, Facebook, Google)
  def get(name:String) = providers.find(_.name == name).getOrElse(Unknown)
}

object Unknown extends Provider{
  val provider: Provider = this
  val name: String = "unknown"
  val consumerToken = ConsumerToken("", "")
  def socialUser(token: Token): SocialUser = InvalidUser
}
