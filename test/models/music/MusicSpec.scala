package models.music

import models.ModelSpecBase
import java.io.{FileInputStream, InputStream, File}

class MusicSpec extends ModelSpecBase {
  val testDataName = "test-data.mp3"
  val testDataPath = s"test/data/$testDataName"

  "Music" should {
    ".apply(File)" >> {
      "ファイル名が取得できる" >> {
        Music(new File(testDataPath)).name must_==(testDataName)
      }
      "ファイルのデータが配列で取得できる" >> {
        val file = new File(testDataPath)
        Music(file).rawData must_==(read(file))
      }
    }
  }

  def read(data: File): Array[Byte] = {
    val buffer = new Array[Byte](data.length.toInt)
    using(new FileInputStream(data)){ _.read(buffer) }
    buffer
  }

  def using(in: InputStream)(f: InputStream => Unit) = {
    try {
      f(in)
    } finally {
      in.close
    }
  }

}
