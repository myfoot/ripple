package models.user

import models.{WithPlayContext, ModelSpecBase}
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.social.twitter.Twitter

class AccessTokenSpec extends ModelSpecBase {
  ".apply" should {
    "tokenとsecretはtrimされる" in new WithPlayContext {
      AccessToken(Twitter, " hoge　", "　 foo hoge  ", 0) must equalTo(AccessToken(Twitter, "hoge", "foo hoge", 0))
    }
  }
  "#validate" should {
    "token" in {
      "空文字不可" in new WithPlayContext {
        AccessToken(Twitter, "", "secret", 0).validate must beLeft
      }
    }
    "secret" in {
      "空文字不可" in new WithPlayContext {
        AccessToken(Twitter, "hoge", "", 0).validate must beLeft
      }
    }
    "unique" in new WithPlayContext {
      "provider & token & secretで一意" in new withTestData {
        AccessToken(requestToken.provider, requestToken.token, requestToken.secret, 0).validate must beLeft
      }
    }
  }
  "#user" should {
    "RequestTokenの保持ユーザーが取得できる" in new withTestData {
      requestToken.user must equalTo(user)
    }
  }

  trait withTestData extends WithPlayContext {
    lazy val user = User("name","email", "pass", Administrator)
    lazy val requestToken = AccessToken(Twitter, "token", "secret", user.id)

    override def before = {
      transaction {
        user.save
        requestToken.save
      }
    }

    override def after = {
      transaction {
        accessTokens.deleteWhere(r => r.id === requestToken.id)
        users.deleteWhere(u => u.id === user.id)
      }
    }
  }

}
