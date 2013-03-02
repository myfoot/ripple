package models.user

import models.SpecBase
import org.specs2.mutable.BeforeAfter
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.social.twitter.Twitter
import models.social.facebook.Facebook

class AccessTokenRepositorySpec extends SpecBase {
  ".find" should {
    "指定された provider & token & secret のデータが存在する場合はSome" in new withSampleData{
      AccessTokenRepository.find(Twitter, token.token, token.secret) must beSome(token)
    }
    "指定された provider & token & secret のデータが存在しない場合はNone" in new withSampleData{
      AccessTokenRepository.find(Facebook, token.token, token.secret) must beNone
    }
  }
  ".insert" should {
    "追加可能な場合はRight" in new withSampleData {
      val reqToken = AccessToken(Facebook, "token", "secret", user.id)
      AccessTokenRepository.insert(reqToken) must beRight(reqToken)
    }
    "追加不可能な場合はLeft" in new withSampleData{
      val reqToken = AccessToken(Facebook, "", "secret", user.id)
      AccessTokenRepository.insert(reqToken) must beLeft
    }
  }

  trait withSampleData extends BeforeAfter {
    lazy val user = User("name","emal","pass", Administrator)
    lazy val token = AccessToken(Twitter, "hoge", "foo", user.id)

    def before = {
      transaction {
        user.save
        token.save
      }
    }

    def after = {
      transaction {
        accessTokens.deleteWhere(u => u.id <> 0)
        users.deleteWhere(u => u.id === user.id)
      }
    }
  }

}
