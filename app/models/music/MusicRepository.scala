package models.music

import models.chat.ChatRoom
import models.CoreSchema._
import org.squeryl._
import PrimitiveTypeMode._

object MusicRepository {

  def find(id: Long): Option[Music] = musics.lookup(id)

  def insert(chatRoom:ChatRoom, music:Music) = music.validate match {
    case x@Left(_) => x
    case x@Right(_) => {
      Right(chatRoom.musics.associate(music))
    }
  }

}
