package models.music

import models.BaseEntity
import java.io.{FileInputStream => Fin, File}

import resource._
import models.util.{ValidatorTypes, Validator}
import models.util.Validations._
import util.string.StringExtension._
import org.squeryl.annotations.Column
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import models.util.audio.{AudioReadException, AudioReader}

class Music(val name: String,
            @Column("raw_data")
            val rawData:Array[Byte],
            @Column("artist_name")
            val artistName: String,
            @Column("album_name")
            val albumName: String,
            @Column("song_title")
            val songTitle: String) extends BaseEntity {
  type ModelClass = Music
  def this(name: String, rawData: Array[Byte]) = this(name.trimSpaces, rawData, "", "", "")

  override lazy val validators: Map[Symbol, Validator] = Map(
    'name -> requiredText(name),
    'rawData -> new Validator {
      def validate: Either[ValidatorTypes.ErrorNames, Any] = if (rawData.nonEmpty) Validator.right else Left(List('empty))
      val continue: Boolean = false
    }
  )
}

object Music {
  def apply(rawData: File) = {
    println(s"Music.apply => rawData is empty? : ${read(rawData).isEmpty}")
    try {
      val audio = AudioReader(rawData)
      new Music(
        name = rawData.getName.trimSpaces,
        rawData = read(rawData),
        artistName = audio.artistName,
        albumName = audio.albumName,
        songTitle = audio.songTitle)
    } catch {
      case e: AudioReadException => e.printStackTrace();InvalidMusic(rawData)
    }
  }

  private def InvalidMusic(data: File) = new Music(data.getName.trimSpaces, Array.emptyByteArray, "", "", "")

  private def read(data: File) = {
    managed(new Fin(data)).map{ in =>
      val buf = new Array[Byte](data.length.toInt)
      in.read(buf)
      buf
    }.opt.getOrElse(Array.emptyByteArray)
  }
}
