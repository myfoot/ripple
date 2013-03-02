package models.user

import org.specs2.mutable._
import org.squeryl._
import PrimitiveTypeMode._
import models.CoreSchema._
import models.SpecBase
import models.social.twitter.Twitter
import models.social.SocialUser

class UserRepositorySpec extends SpecBase {
  "UserRepository" should {
    ".find" in {
      "指定された名前＆パスワードのユーザーが存在する場合はUserオブジェクトを返す" in new sampleUser {
        UserRepository.find(user.name, user.password) must beSome(user)
      }
      "指定された名前＆パスワードのユーザーが存在しない場合は何も返さない" in new sampleUser {
        UserRepository.find("", "") must beNone
      }
    }
    ".findById" in {
      "指定された名前＆パスワードのユーザーが存在する場合はUserオブジェクトを返す" in new sampleUser {
        println(UserRepository.findById(user.id).map(_.role))
        UserRepository.findById(user.id) must beSome(user)
      }
      "指定された名前＆パスワードのユーザーが存在しない場合は何も返さない" in new sampleUser {
        UserRepository.findById(user.id+1) must beNone
      }
    }
    ".insert" in {
      "追加可能な場合はRight" in {
        UserRepository.insert(validUser) must beRight(validUser)
      }
      "追加不可能な場合はLeft" in {
        UserRepository.insert(invalidUser) must beLeft
      }
    }
    ".insertAsSocialUser" in {
      "登録可能な場合はUserとAccessTokenを登録する" in {
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

  trait sampleUser extends BeforeAfter {
    val user = User("hoge", "foo", "bar", Administrator)

    def before = {
      transaction {
        user.save
      }
    }

    def after = {
      transaction {
        users.deleteWhere(u => u.id === user.id)
      }
    }
  }
}
