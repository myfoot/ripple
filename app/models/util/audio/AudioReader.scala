package models.util.audio

import java.io.File
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import models.util.audio.AudioReader.ErrorMessages

class AudioReader(data: File) {
  private lazy val audio = {
    try {
      AudioFileIO.read(data)
    } catch {
      case e: Throwable => throw new AudioReadException(ErrorMessages.NotMusicFile, e)
    }
  }
  private lazy val tag = audio.getTag

  lazy val artistName = tagValue(FieldKey.ARTIST)
  lazy val albumName = tagValue(FieldKey.ALBUM)
  lazy val songTitle = tagValue(FieldKey.TITLE)

  private def tagValue(key: FieldKey) = tag.getFirst(key)
}
object AudioReader {
  def apply(data: File) = new AudioReader(data)
  object ErrorMessages {
    val NotMusicFile = "it is not a music file."
  }
}
