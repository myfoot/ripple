package models.user

import models.BaseEntity

class User(val name: String,
           val email: String,
           val password: String,
           roleName: String) extends BaseEntity {

  def this() = this("", "", "", GuestUser.name)
  def role = Role.get(roleName)
}

object User{
  def apply(name:String, email:String, password: String, role:Role) = new User(name,email,password, role.name)
}