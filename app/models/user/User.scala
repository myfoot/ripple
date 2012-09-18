package models.user

import models.BaseEntity

class User(val name: String,
           val email: String,
           val password: String,
           valid: Boolean = true) extends BaseEntity {
  def this() = this("", "", "")
  def isValid = valid
}

object User{
  def apply(name:String, email:String, password: String, valid: Boolean = true) = new User(name,email,password, valid)
}

object UserFactory {
  def invalidUser = new User("","","",false)
}