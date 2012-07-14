package models

case class User(name: String,
                email: String,
                password: String) extends BaseEntity {
  def this() = this("", "", "")
}