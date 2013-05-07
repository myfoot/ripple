package models.chat

import models.{CoreSchema, BaseEntity}
import models.user.User
import collection.mutable.{Set => MutableSet}
import util.string.StringExtension._
import models.util.Validator
import models.util.Validations._
import models.util.ValidatorComposite._
import models.music.Music
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._
import org.squeryl.dsl.ManyToOne

class ChatRoom(val name:String,
               @Column("owner_id")
               val ownerId: Long) extends BaseEntity{
  type ModelClass = this.type

  private type Members = MutableSet[User]
  // TODO: DBかKVSにStoreしてここでは管理しない
  lazy val members = MutableSet[User]()

  override lazy val validators: Map[Symbol, Validator] = Map(
    'name -> (requiredText(name, false) :+ unique(ChatRoomRepository.find(name)))
  )

  lazy val owner: ManyToOne[User] = CoreSchema.userToChatRoom.right(this)

  def musics = CoreSchema.chatRoomToMusic.left(this)
  def musicsWithoutRawData: Seq[(Long, String, String, String, String)] = {
    from(musics) { m =>
      where(m.chatRoomId === this.id)
      select(m.id, m.name, m.artistName, m.albumName, m.songTitle)
    }.toList
  }

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
  def apply(name:String, user:User) = new ChatRoom(name.trimSpaces, user.id)
}
