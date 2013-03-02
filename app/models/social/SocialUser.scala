package models.social

trait SocialUser {
  def name: String
  def profileImageUrl: String
  val provider: Provider
}

object InvalidUser extends SocialUser {
  def name: String = "invalid-name"
  def profileImageUrl: String = "invalid-url"
  val provider: Provider = Unknown
}
