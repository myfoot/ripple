package models

import chat.ChatRoom
import user.{AccessToken, User}

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
  val accessTokens = table[AccessToken]("access_token")

  on(users)(ent => declare(
    ent.id is(autoIncremented),
    ent.email is(unique, dbType("varchar(255)")),
    ent.password is(dbType("varchar(255)"))
  ))

  on(chatRooms)(ent => declare(
    ent.id is(autoIncremented),
    ent.name is(unique, dbType("varchar(255)"))
  ))

  on(accessTokens)(ent => declare(
    ent.id is(autoIncremented),
    ent.providerName is(dbType("varchar(255)")),
    ent.userId is(dbType("varchar(255)")),
    ent.token is(unique, dbType("varchar(255)")),
    ent.secret is(unique, dbType("varchar(255)"))
  ))

  val userToRequestToken =
    oneToManyRelation(users, accessTokens)
      .via((user, token) => user.id === token.userId)
}