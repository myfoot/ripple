package models.music

import models.chat.ChatRoom

object MusicRepository {
  def insert(chatRoom:ChatRoom, music:Music) = music.validate match {
    case x@Left(_) => x
    case x@Right(_) => {
      Right(chatRoom.musics.associate(music))
    }
  }
}
