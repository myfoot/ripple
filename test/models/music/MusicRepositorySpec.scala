package models.music

import models.{ModelSpecBase}
import models.util.RequireTextValidator
import models.chat.ChatRoom
import models.CoreSchema._
import java.io.File
import org.squeryl.PrimitiveTypeMode._
import org.specs2.mutable.BeforeAfter


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
      "追加可能な場合はRightオブジェクトが取得できる" >> new withData {
        MusicRepository.insert(chatRoom, validMusic) must beRight(validMusic)
      }
      "追加不可能な場合はLeftオブジェクトが取得できる" >> new withData {
        MusicRepository.insert(chatRoom, invalidMusic) must beLeft(Map('name -> List(RequireTextValidator.KEY), 'rawData -> List('empty)))
      }
    }
  }

  trait withData extends WithTransaction {
    var chatRoom = ChatRoom("aaa")

    override def before = {
      chatRoom.save
    }
    override def after = {
      musics.foreach{music => musics.deleteWhere(m => m.id === music.id) }
      chatRooms.deleteWhere(c => c.id === chatRoom.id)
    }
  }
}
