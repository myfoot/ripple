package models.social

case class Token(token: String, secret: String)

object Token {
  val invalid = Token("", "")
}
