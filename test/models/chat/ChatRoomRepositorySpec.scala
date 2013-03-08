package models.chat

import models.{WithPlayContext, SpecBase}
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/12/02
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
class ChatRoomRepositorySpec extends SpecBase {
  "ChatRoomRepository" should {
    "#find" in {
      "指定された名前の部屋が存在する場合はCharRoomオブジェクトを返す" in new withTestData {
        ChatRoomRepository.find(chatRoom.name) must beSome(chatRoom)
      }
      "指定された名前の部屋が存在しない場合は何も返さない" in new withTestData {
        ChatRoomRepository.find("hogefoobar") must beNone
      }
    }
    "#find(id)" in {
      "指定したIDの部屋が存在する場合はSome(ChatRoom)が取得できる" in new withTestData {
        ChatRoomRepository.find(chatRoom.id) must beSome(chatRoom)
      }
      "指定したIDの部屋が存在しない場合はNoneが返る" in new withTestData {
        ChatRoomRepository.find(-1) must beNone
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

    "#create" in {
      "指定されたChatRoomオブジェクトが追加可能な場合はRightオブジェクトが取得できる" in new withTestData {
        // TODO: 本当はstub化したいが、mixinしたtraitのメソッドをstub化しようとすると、エラーになる。UninitializedFieldError: Uninitialized field: Schema.scala: 11 (Validations.scala:23)
//        val mockChatRoom = mock[ChatRoom]
//        mockChatRoom.validate returns true
        val mockChatRoom = ChatRoom("hoge-chat")
        ChatRoomRepository.insert(mockChatRoom) must beRight(mockChatRoom)
        ChatRoomRepository.find(mockChatRoom.name) must beSome
      }
      "指定されたChatRoomオブジェクトが追加不可能な場合はLeftオブジェクトが取得できる" in new withTestData {
        // TODO: stub化
        val mockChatRoom = ChatRoom(chatRoom.name)
        ChatRoomRepository.insert(mockChatRoom) must beLeft
        ChatRoomRepository.find(mockChatRoom.name) must beSome(chatRoom)
      }
    }

    "#all" in {
      "全ての部屋が取得できる" in new withTestData {
        ChatRoomRepository.all.size must equalTo(2)
      }
    }
  }

  trait withTestData extends WithPlayContext {
    lazy val chatRoom = ChatRoom("test_room")
    lazy val chatRoom2 = ChatRoom("test_room2")
    override def before = {
      transaction {
        chatRoom.save
        chatRoom2.save
      }
    }

    override def after = {
      transaction {
        chatRooms.toList.foreach(c => chatRooms.deleteWhere(c2 => c2.id === c.id))
      }
    }
  }
}
