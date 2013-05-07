package models

import chat.ChatRoom
import music.Music
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
  val musics = table[Music]("music")

  on(users)(ent => declare(
    ent.id is(autoIncremented),
    ent.email is(unique, dbType("varchar(255)")),
    ent.password is(dbType("varchar(255)"))
  ))

  on(chatRooms)(ent => declare(
    ent.id is(autoIncremented),
    ent.name is(unique, dbType("varchar(255)")),
    ent.ownerId is(dbType("int(10)"))
  ))

  on(accessTokens)(ent => declare(
    ent.id is(autoIncremented),
    ent.providerName is(dbType("varchar(255)")),
    ent.userId is(dbType("varchar(255)")),
    ent.token is(unique, dbType("varchar(255)")),
    ent.secret is(unique, dbType("varchar(255)"))
  ))

  on(musics)(ent => declare(
    ent.id is(autoIncremented),
    ent.rawData is(dbType("blob")),
    ent.albumName  is(dbType("varchar(255)")),
    ent.artistName is(dbType("varchar(255)")),
    ent.songTitle  is(dbType("varchar(255)")),
    ent.chatRoomId is(dbType("int(10)"))
  ))

  val userToRequestToken =
    oneToManyRelation(users, accessTokens)
      .via((user, token) => user.id === token.userId)

  val chatRoomToMusic =
    oneToManyRelation(chatRooms, musics)
      .via((chat, music) => chat.id === music.chatRoomId)

  val userToChatRoom =
    oneToManyRelation(users, chatRooms)
      .via((user, chat) => user.id === chat.ownerId)
}