package models.music

import models.BaseEntity
import java.io.{FileInputStream => Fin, File}

import resource._

class Music(val name: String, val rawData:Array[Byte]) extends BaseEntity

object Music {
  def apply(rawData: File) = {
    val file = managed(new Fin(rawData)).map{ in =>
      val buf = new Array[Byte](rawData.length.toInt)
      in.read(buf)
      buf
    }.opt.getOrElse(Array.emptyByteArray)
    new Music(rawData.getName, file)
  }
}
