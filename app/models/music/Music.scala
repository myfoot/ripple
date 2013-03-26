package models.music

import models.BaseEntity
import java.io.{FileInputStream => Fin, File}

import resource._
import models.util.{ValidatorTypes, Validator}
import models.util.Validations._
import util.string.StringExtension._
import org.squeryl.annotations.Column

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
    val file = managed(new Fin(rawData)).map{ in =>
      val buf = new Array[Byte](rawData.length.toInt)
      in.read(buf)
      buf
    }.opt.getOrElse(Array.emptyByteArray)
    new Music(rawData.getName.trimSpaces, file, "", "", "")
  }
}
