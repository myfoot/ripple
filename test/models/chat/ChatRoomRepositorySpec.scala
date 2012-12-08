package models.chat

import models.RepositorySpecBase
import models.user.{Administrator, User, UserRepository}
import org.specs2.mutable.BeforeAfter
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/12/02
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
class ChatRoomRepositorySpec extends RepositorySpecBase {
  "ChatRoomRepository" should {
    "#find" in {
      "指定された名前の部屋が存在する場合はCharRoomオブジェクトを返す" in new withTestData {
        ChatRoomRepository.find(chatRoom.name) must beSome(chatRoom)
      }
      "指定された名前の部屋が存在しない場合は何も返さない" in new withTestData {
        ChatRoomRepository.find("hogefoobar") must beNone
      }
    }
    "#findOrCreate" in {
      "指定された名前の部屋が存在する場合はCharRoomオブジェクトを返す" in new withTestData {
        ChatRoomRepository.findOrCreate(chatRoom.name) must equalTo(chatRoom)
      }
      "指定された名前の部屋が存在しない場合は新規作成して返す" in new withTestData {
        val name = "hogefoobar"
        val newRoom = ChatRoomRepository.findOrCreate(name)
        newRoom.name must equalTo(name)
        newRoom.isPersisted must beTrue
      }
    }
  }

  trait withTestData extends BeforeAfter {
    lazy val chatRoom = ChatRoom("test_room")
    def before = {
      transaction {
        chatRoom.save
      }
    }

    def after = {
      transaction {
        chatRooms.deleteWhere(c => c.id === chatRoom.id)
      }
    }
  }
}
