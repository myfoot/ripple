package models.user

import models.BaseEntity

case class User(name: String,
                email: String,
                password: String) extends BaseEntity {
  def this() = this("", "", "")
}