package models

import chat.ChatRoom
import user.User

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import models.util.Validations

class BaseEntity extends KeyedEntity[Long] with Validations {
  val id: Long = 0
  override def equals(obj:Any) = {
    obj.isInstanceOf[BaseEntity] && obj.asInstanceOf[BaseEntity].id == this.id
  }
}

object CoreSchema extends Schema {
  val users = table[User]("user")
  val chatRooms = table[ChatRoom]("chat_room")

  on(users)(ent => declare(
    ent.id is(autoIncremented),
    ent.email is(unique, dbType("varchar(255)"))
  ))

  on(chatRooms)(ent => declare(
    ent.id is(autoIncremented),
    ent.name is(unique, dbType("varchar(255)"))
  ))
}