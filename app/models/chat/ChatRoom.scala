package models.chat

import models.BaseEntity
import models.user.User
import collection.mutable.{Set => MutableSet}

class ChatRoom(val name:String) extends BaseEntity {
  private type Members = MutableSet[User]
  // TODO: DBかKVSにStoreしてここでは管理しない
  lazy val members = MutableSet[User]()

  def join(user:User):Boolean = updateMembers({_ contains user}, {_ += user})
  def leave(user:User):Boolean = updateMembers({x => !(x contains user)}, {_ -= user})

  private def updateMembers(condition: Members => Boolean, logic: Members => Unit) = members match {
    case x if condition(x) => false
    case x@_ => {
      logic(x)
      true
    }
  }
}

object ChatRoom {
  def apply(name:String) = new ChatRoom(name)
}