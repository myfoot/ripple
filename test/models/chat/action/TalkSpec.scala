package models.chat.action

import models.{WithTestChatRoom, ModelSpecBase}
import org.json4s.JsonDSL._
import org.json4s.JsonAST.JValue

class TalkSpec extends ModelSpecBase {
  "Talk" should {
    "#toJson" >> {
      "jsonが取得できる" >> new WithTestData {
        val json: JValue = ("userId" -> user.id) ~ ("message" -> "hoge")
        Talk(user, "hoge").toJson must_== json
      }
    }
  }

  trait WithTestData extends WithTransaction with WithTestChatRoom {
    override def before { save }
    override def after { clean }
  }
}
