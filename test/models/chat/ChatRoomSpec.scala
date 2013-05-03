package models.chat

import models.user.{Administrator, User}
import models.ModelSpecBase
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.music.Music
import java.io.File

class ChatRoomSpec extends ModelSpecBase {
  lazy val user = User("hoge", "hoge@foo.com", "pass", Administrator)

  ".apply" should {
    "nameは前後の半角全角スペースがtrimされる" >> {
      ChatRoom("  　　hoge 　foo 　　").name must equalTo("hoge 　foo")
    }
  }

  "#join" should {
    "未入室のユーザーは参加する事ができる" >> {
      new ChatRoom("hoge").join(user) must beTrue
    }
    "入室済のユーザーは参加する事ができない" >> {
      val chatRoom = new ChatRoom("hoge")
      chatRoom.join(user)
      chatRoom.join(user) must beFalse
    }
  }
  "#leave" should {
    "未入室のユーザーは退室する事ができない" >> {
      new ChatRoom("hoge").leave(user) must beFalse
    }
    "入室済のユーザーは参加する事ができる" >> {
      val chatRoom = new ChatRoom("hoge")
      chatRoom.join(user)
      chatRoom.leave(user) must beTrue
    }
  }
  "#validation" should {
    "name" >> {
      "名前が空白のみの場合は作成できない" >> {
        verifyRequiredText(ChatRoom(_))
      }
      "同じ名前の部屋は作成できない" >> new ValidationTestWithTestData {
        val target: ChatRoom = ChatRoom("")
        expectFailed()
      }
      "同じ名前の部屋が存在しない場合は作成可能" >> new ValidationTest[ChatRoom] {
        val target = ChatRoom("test_room2")
        expectSuccess()
      }
    }
  }
  "#musicsWithoutRawData" >> {
    val mp3TestDataName = "test-data.mp3"
    val mp3TestDataPath = s"test/data/$mp3TestDataName"
    "自身に紐づく音楽データを取得する（生データは含まない）" >> new WithTestData {
      inTransaction {
        val music = Music(new File(mp3TestDataPath))
        chatRoom.musics.associate(music)
        val actual = chatRoom.musicsWithoutRawData
        actual.length must be_==(1)
        actual.head must be_==(music.id, music.name, music.artistName, music.albumName, music.songTitle)
      }
    }
  }

  trait WithTestData extends WithTransaction {
    lazy val chatRoom = ChatRoom("test_room")

    override def before = {
      chatRoom.save
    }

    override def after = {
      chatRooms.toList.foreach{c =>
        c.musics.deleteAll
        chatRooms.deleteWhere(c2 => c2.id === c.id)
      }
    }
  }

  trait ValidationTestWithTestData extends ValidationTest[ChatRoom] {
    lazy val chatRoom = ChatRoom("test_room")

    override def before = {
      chatRoom.save
    }

    override def after = {
      chatRooms.toList.foreach(c => chatRooms.deleteWhere(c2 => c2.id === c.id))
    }
  }

}
