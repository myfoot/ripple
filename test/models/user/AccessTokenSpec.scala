package models.user

import models.{ModelSpecBase}
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.social.twitter.Twitter
import org.specs2.mutable.BeforeAfter

class AccessTokenSpec extends ModelSpecBase {
  ".apply" should {
    "tokenとsecretはtrimされる" >> {
      AccessToken(Twitter, " hoge　", "　 foo hoge  ", 0) must equalTo(AccessToken(Twitter, "hoge", "foo hoge", 0))
    }
  }
  "#validate" should {
    "token" >> {
      "空文字不可" >> {
        verifyRequiredText(AccessToken(Twitter, _, "secret", 0))
      }
    }
    "secret" >> {
      "空文字不可" >> {
        verifyRequiredText(AccessToken(Twitter, "hoge", _, 0))
      }
    }
    "unique" >> {
      "provider & token & secretで一意" >> new withTestData {
        AccessToken(requestToken.provider, requestToken.token, requestToken.secret, 0).validate must beLeft
      }
    }
  }
  "#user" should {
    "RequestTokenの保持ユーザーが取得できる" >> new withTestData {
      requestToken.user must equalTo(user)
    }
  }

  trait withTestData extends WithTransaction {
    lazy val user = User("name","email", "pass", Administrator)
    lazy val requestToken = AccessToken(Twitter, "token", "secret", user.id)

    override def before = {
      user.save
      requestToken.save
    }

    override def after = {
      accessTokens.deleteWhere(r => r.id === requestToken.id)
      users.deleteWhere(u => u.id === user.id)
    }
  }

}
