package models.social

trait SocialConfig {
  // FIXME: model層がPlayに依存したくないので、util-evalを使いたいがScala2.10.0に対応していないので後でやる
  private lazy val conf = play.Play.application.configuration
  private val socialConfKey = "social"
  lazy val requestTokenUrl: String = conf.getString("%s.%s.requestTokenUrl".format(socialConfKey, provider.name))
  lazy val accessTokenUrl: String = conf.getString("%s.%s.accessTokenUrl".format(socialConfKey, provider.name))
  lazy val authorizationUrl: String = conf.getString("%s.%s.authorizationUrl".format(socialConfKey, provider.name))
  lazy val consumerKey: String = conf.getString("%s.%s.consumerKey".format(socialConfKey, provider.name))
  lazy val consumerSecret: String = conf.getString("%s.%s.consumerSecret".format(socialConfKey, provider.name))
  val provider: Provider
}
