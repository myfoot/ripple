package models.user

import models.SpecBase
import org.specs2.mutable.BeforeAfter
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.social.twitter.Twitter

class AccessTokenSpec extends SpecBase {
  ".apply" should {
    "tokenとsecretはtrimされる" in {
      AccessToken(Twitter, " hoge　", "　 foo hoge  ", 0) must equalTo(AccessToken(Twitter, "hoge", "foo hoge", 0))
    }
  }
  "#validate" should {
    "token" in {
      "空文字不可" in {
        AccessToken(Twitter, "", "secret", 0).validate must beLeft
      }
    }
    "secret" in {
      "空文字不可" in {
        AccessToken(Twitter, "hoge", "", 0).validate must beLeft
      }
    }
    "unique" in {
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

  trait withTestData extends BeforeAfter {
    lazy val user = User("name","email", "pass", Administrator)
    lazy val requestToken = AccessToken(Twitter, "token", "secret", user.id)

    def before = {
      transaction {
        user.save
        requestToken.save
      }
    }

    def after = {
      transaction {
        accessTokens.deleteWhere(r => r.id === requestToken.id)
        users.deleteWhere(u => u.id === user.id)
      }
    }
  }

}
