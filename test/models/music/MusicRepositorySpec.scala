package models.music

import models.{WithPlayContext, ModelSpecBase}
import java.io.File
import models.util.RequireTextValidator

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2013/03/23
 * Time: 0:49
 * To change this template use File | Settings | File Templates.
 */
class MusicRepositorySpec extends ModelSpecBase {
  val testdata = "test/data/test-data.mp3"
  lazy val validMusic = Music(new File(testdata))
  lazy val invalidMusic = new Music("", Array.emptyByteArray)
  "MusicRepository" should {
    ".insert" >> {
      "追加可能な場合はRightオブジェクトが取得できる" >> new WithPlayContext {
        MusicRepository.insert(validMusic) must beRight(validMusic)
      }
      "追加不可能な場合はLeftオブジェクトが取得できる" >> new WithPlayContext {
        MusicRepository.insert(invalidMusic) must beLeft(Map('name -> List(RequireTextValidator.KEY), 'rawData -> List('empty)))
      }
    }
  }
}
