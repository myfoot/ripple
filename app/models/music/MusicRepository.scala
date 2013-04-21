package models.music

import models.CoreSchema._
import org.squeryl._
import PrimitiveTypeMode._
import models.util.ValidatorTypes._
import models.chat.ChatRoom

object MusicRepository {
  def insert(chatRoom:ChatRoom, music:Music) = inTransaction{
    music.validate match {
      case x@Left(_) => x
      case x@Right(_) => {
        Right(chatRoom.musics.associate(music))
      }
    }
  }
}
