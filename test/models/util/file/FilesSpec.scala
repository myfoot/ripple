package models.util.file

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import java.io.File

class FilesSpec extends Specification with Mockito {
  "Files" should {
    ".deleteAfter" >> {
      "関数実行後にファイルを削除する" >> {
        verify()
      }
      "例外発生時もファイルは削除される" >> {
        verify(throw new RuntimeException)
      }
    }

    def verify(f: => Unit = {}) = {
      val file = File.createTempFile("hoge", "hoge")
      try {
        val mockObj = mock[Hoge]

        file.exists must beTrue

        try {
          Files.deleteAfter(file) { tmp =>
            mockObj.name
            f
          }
        } catch {
          case e: Throwable =>
        }
        there was one(mockObj).name
        file.exists must beFalse
      } finally {
        file.delete
      }
    }
  }

  class Hoge {
    def name = "hoge"
  }
}
