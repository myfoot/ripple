package models.user

import models.{WithPlayContext, ModelSpecBase}
import org.specs2.mutable.BeforeAfter
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.social.twitter.Twitter
import models.social.facebook.Facebook
import models.social.Unknown
import org.codehaus.jackson.map.`type`.TypeFactory
import org.specs2.execute.{Result, AsResult}

class AccessTokenRepositorySpec extends ModelSpecBase {
  ".find" should {
    "指定された provider & token & secret のデータが存在する場合はSome" >> new withSampleData{
      AccessTokenRepository.find(token.provider, token.token, token.secret) must beSome(token)
    }
    "指定された provider & token & secret のデータが存在しない場合はNone" >> new withSampleData{
      AccessTokenRepository.find(Unknown, "other-token", token.secret) must beNone
    }
  }
  ".insert" should {
    "追加可能な場合はRight" >> new withSampleData {
      val reqToken = AccessToken(Facebook, "token", "secret", user.id)
      AccessTokenRepository.insert(reqToken) must beRight(reqToken)
    }
    "追加不可能な場合はLeft" >> new withSampleData{
      val reqToken = AccessToken(Facebook, "", "secret", user.id)
      AccessTokenRepository.insert(reqToken) must beLeft
    }
  }

  trait withSampleData extends WithPlayContext {
    lazy val user = User("name","emal","pass", Administrator)
    lazy val token = AccessToken(Twitter, "hoge", "foo", user.id)

    override def before = {
      transaction {
        user.save
        token.save
      }
    }
    override def after = {
      transaction {
        accessTokens.deleteWhere(u => u.id <> 0)
        users.deleteWhere(u => u.id === user.id)
      }
    }
  }

}
