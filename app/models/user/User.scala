package models.user

import models.BaseEntity
import models.util.Validator
import models.util.Validations._
import util.string.StringExtension._

class User(val name: String,
           val email: String,
           val password: String,
           roleName: String) extends BaseEntity {
  type ModelClass = User
  def this() = this("", "", "", GuestUser.name)

  override lazy val validators: Map[Symbol, Validator] = Map(
    'name -> requiredText(name),
    'password -> requiredText(password)
  )

  def role = Role.get(roleName)
}

object User{
  def apply(name:String, email:String, password: String, role:Role) = new User(name.trimSpaces(),email.trimSpaces(),password.trimSpaces(), role.name)
}