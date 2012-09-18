package models.user

import org.specs2.mutable._
import org.squeryl._
import PrimitiveTypeMode._
import models.CoreSchema._
import models.RepositorySpecBase

class UserRepositorySpec extends RepositorySpecBase {
  "UserRepository" should {
    "#find" in {
      "指定された名前＆パスワードのユーザーが存在する場合はUserオブジェクトを返す" in new sampleUser {
        UserRepository.find(user.name, user.password) must beSome(user)
      }
      "指定された名前＆パスワードのユーザーが存在しない場合は何も返さない" in new sampleUser {
        UserRepository.find("", "") must beNone
      }
    }
    "#findById" in {
      "指定された名前＆パスワードのユーザーが存在する場合はUserオブジェクトを返す" in new sampleUser {
        UserRepository.findById(user.id) must beSome(user)
      }
      "指定された名前＆パスワードのユーザーが存在しない場合は何も返さない" in new sampleUser {
        UserRepository.findById(user.id+1) must beNone
      }
    }
  }

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
