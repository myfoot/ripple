package models.user

import org.squeryl._
import PrimitiveTypeMode._
import models.CoreSchema._
import models.ModelSpecBase
import models.social.twitter.Twitter
import models.social.SocialUser

class UserRepositorySpec extends ModelSpecBase {
  "UserRepository" should {
    ".find" >> {
      "指定された名前＆パスワードのユーザーが存在する場合はUserオブジェクトを返す" >> new WithTestData {
        UserRepository.find(user.name, user.password) must beSome(user)
      }
      "指定された名前＆パスワードのユーザーが存在しない場合は何も返さない" >> new WithTestData {
        UserRepository.find("", "") must beNone
      }
    }
    ".findById" >> {
      "指定された名前＆パスワードのユーザーが存在する場合はUserオブジェクトを返す" >> new WithTestData {
        println(UserRepository.findById(user.id).map(_.role))
        UserRepository.findById(user.id) must beSome(user)
      }
      "指定された名前＆パスワードのユーザーが存在しない場合は何も返さない" >> new WithTestData {
        UserRepository.findById(user.id+1) must beNone
      }
    }
    ".insert" >> {
      "追加可能な場合はRight" >> new WithTransaction {
        UserRepository.insert(validUser) must beRight(validUser)
      }
      "追加不可能な場合はLeft" >> new WithTransaction {
        UserRepository.insert(invalidUser) must beLeft
      }
    }
    ".insertAsSocialUser" >> {
      "登録可能な場合はUserとAccessTokenを登録する" >> {
        val name = "hoge"
        val token = "aaaaaaaa"
        val secret = "bbbbbbbb"
        val socialUser = mock[SocialUser]
        socialUser.provider returns Twitter
        socialUser.name returns name

        val result = UserRepository.insertAsSocialUser(socialUser, token, secret)
        result match {
          case Right((user:User, token:AccessToken)) => {
            user.isPersisted must beTrue
            token.isPersisted must beTrue
          }
          case _ => failure
        }
        result must beRight // これ要らないんだけど、無いと戻り値の型が違うので怒られる、、、
      }
    }
  }

  // TODO: mock
  lazy val validUser = User("name", "email", "pass", Administrator)
  lazy val invalidUser = User("", "", "pass", Administrator)

  trait WithTestData extends WithTransaction {
    val user = User("hoge", "foo", "bar", Administrator)

    override def before = {
      user.save
    }

    override def after = {
      users.deleteWhere(u => u.id === user.id)
    }
  }
}
