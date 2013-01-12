package models.chat

import models.BaseEntity
import models.user.User
import collection.mutable.{Set => MutableSet}
import collection.mutable

class ChatRoom(val name:String) extends BaseEntity{
  type ModelClass = this.type

  private type Members = MutableSet[User]
  // TODO: DBかKVSにStoreしてここでは管理しない
  lazy val members = MutableSet[User]()

  // TODO: messageの外出し（フレームワークに依存したくない、、、ってかここで定義する必要がない？？）
  validation('name, notEmptyText(name), "must be not empty", false)
  validation('name, unique(ChatRoomRepository.find(name)), "already registered : %s".format(name))

  def join(user:User):Boolean = updateMembers({_ contains user}, {_ += user})
  def leave(user:User):Boolean = updateMembers({x => !(x contains user)}, {_ -= user})
  private def updateMembers(condition: Members => Boolean, logic: Members => Unit) = members match {
    case x if condition(x) => false
    case x@_ => {
      logic(x)
      true
    }
  }
  def toMap = Map("id" -> id, "name" -> name)
}

object ChatRoom {
  def apply(name:String) = new ChatRoom(name)
}
