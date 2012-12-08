package models.chat

import org.specs2.mutable.{Before, Specification}
import models.user.{Administrator, Role, User}

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/12/08
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
class ChatRoomSpec extends Specification {
  lazy val user = User("hoge", "hoge@foo.com", "pass", Administrator)
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
}
