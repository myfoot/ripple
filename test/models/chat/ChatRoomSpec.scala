package models.chat

import org.specs2.mutable.{BeforeAfter, Before, Specification}
import models.user.{Administrator, Role, User}
import models.SpecBase
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/12/08
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
class ChatRoomSpec extends SpecBase {
  lazy val user = User("hoge", "hoge@foo.com", "pass", Administrator)

  ".apply" should {
    "nameは前後の半角全角スペースがtrimされる" in {
      ChatRoom("  　　hoge 　foo 　　").name must equalTo("hoge 　foo")
    }
  }

  "#join" should {
    "未入室のユーザーは参加する事ができる" in {
      new ChatRoom("hoge").join(user) must beTrue
    }
    "入室済のユーザーは参加する事ができない" in {
      val chatRoom = new ChatRoom("hoge")
      chatRoom.join(user)
      chatRoom.join(user) must beFalse
    }
  }
  "#leave" should {
    "未入室のユーザーは退室する事ができない" in {
      new ChatRoom("hoge").leave(user) must beFalse
    }
    "入室済のユーザーは参加する事ができる" in {
      val chatRoom = new ChatRoom("hoge")
      chatRoom.join(user)
      chatRoom.leave(user) must beTrue
    }
  }
  "#validation" should {
    "name" in {
      "名前は1文字以上でなければ作成できない" in new withTestData {
        ChatRoom("").validate must beLeft
      }
      "名前が空白のみの場合は作成できない" in new withTestData {
        ChatRoom(" ").validate must beLeft
        ChatRoom("　").validate must beLeft
        ChatRoom("     ").validate must beLeft
      }
      "同じ名前の部屋は作成できない" in new withTestData {
        ChatRoom("test_room").validate must beLeft
      }
      "同じ名前の部屋が存在しない場合は作成可能" in new withTestData {
        val chat = ChatRoom("test_room2")
        chat.validate must beRight(chat)
      }
    }
  }
  "#toMap" should {
    "idとnameのMapが取得できる" in new withTestData {
      chatRoom.toMap must equalTo(Map("id" -> chatRoom.id, "name" -> chatRoom.name))
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
        chatRooms.toList.foreach(c => chatRooms.deleteWhere(c2 => c2.id === c.id))
      }
    }
  }

}
