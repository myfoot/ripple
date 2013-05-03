package models.user

import models.{CoreSchema, BaseEntity}
import models.util.Validator
import models.util.Validations._
import models.social.Provider
import util.string.StringExtension._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Column

// TODO: providerName はEnumration使った方が良さげ。だた、マッピング方法がよくわからないので一旦保留
case class AccessToken(
  @Column("provider")
  providerName: String,
  token: String,
  secret: String,
  @Column("user_id")
  userId: Long) extends BaseEntity {

  type ModelClass = AccessToken
  lazy val provider = Provider.get(providerName)

  override lazy val validators: Map[Symbol, Validator] = Map(
    'token -> requiredText(token),
    'secret -> requiredText(secret),
    'self -> unique(AccessTokenRepository.find(provider, token, secret))
  )

  // headOptionするとCoreSchemaがNoClassDefFoundで実行時にエラーになる為、singleにしておく
  def user : Option[User] = CoreSchema.userToRequestToken.right(this).headOption
}

object AccessToken {
  def apply(provider: Provider, token: String, secret: String, userId: Long) = new AccessToken(provider.name, token.trimSpaces, secret.trimSpaces, userId)
}
