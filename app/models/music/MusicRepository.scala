package models.music

import models.CoreSchema._
import org.squeryl._
import PrimitiveTypeMode._
import models.util.ValidatorTypes._

object MusicRepository {
  def insert(music:Music) = inTransaction{
    music.validate match {
      case x@Left(_) => x
      case x@Right(_) => {
        musics.insert(music)
        x
      }
    }
  }
}
