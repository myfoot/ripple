package models.social.twitter

import models.social.{Provider, Token, SocialUser}

class TwitterUser(client: twitter4j.Twitter, token: Token) extends SocialUser {
  val provider: Provider = Twitter
  lazy val user = client.showUser(client.getScreenName)
  def name: String = user.getScreenName
  def profileImageUrl: String = user.getProfileImageURL
}
