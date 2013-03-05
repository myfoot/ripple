package models.social.twitter

import models.social._
import models.social.ConsumerToken
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

object Twitter extends Provider {
  val provider: Provider = this
  val name: String = "twitter"
  val consumerToken = ConsumerToken(consumerKey,consumerSecret)

  def socialUser(token: Token): SocialUser = {
    val conf = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(Twitter.consumerToken.token)
      .setOAuthConsumerSecret(Twitter.consumerToken.secret)
      .setOAuthAccessToken(token.token)
      .setOAuthAccessTokenSecret(token.secret)
      .build;
    new TwitterUser(new TwitterFactory(conf).getInstance, token)
  }
}