package models.social

import com.typesafe.config.ConfigFactory
import java.io.File


trait ProviderConfig {
  val conf = ConfigFactory.load(ConfigFactory.parseFileAnySyntax(new File("conf/social.conf")))
  val socialConfKey = "social"
  lazy val requestTokenUrl: String = conf.getString("%s.%s.requestTokenUrl".format(socialConfKey, provider.name))
  lazy val accessTokenUrl: String = conf.getString("%s.%s.accessTokenUrl".format(socialConfKey, provider.name))
  lazy val authorizationUrl: String = conf.getString("%s.%s.authorizationUrl".format(socialConfKey, provider.name))
  lazy val consumerKey: String = conf.getString("%s.%s.consumerKey".format(socialConfKey, provider.name))
  lazy val consumerSecret: String = conf.getString("%s.%s.consumerSecret".format(socialConfKey, provider.name))
  val provider: Provider
}
