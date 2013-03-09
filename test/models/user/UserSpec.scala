package models.user

import models.ModelSpecBase

class UserSpec extends ModelSpecBase {
  "User" should {
    "#validate" in {
      "name" in {
        "空文字不可" in {
          User("", "email", "password", Administrator).validate must beLeft
          User(" ", "email", "password", Administrator).validate must beLeft
          User("　", "email", "password", Administrator).validate must beLeft
        }
      }
      "email" in {
        "emailの形式以外は登録不可" in {
          failure
        }.pendingUntilFixed("formatチェックはTODO")
      }
      "password" in {
        "空文字不可" in {
          User("hoge", "email", "", Administrator).validate must beLeft
          User("hoge", "email", " ", Administrator).validate must beLeft
          User("hoge", "email", "　", Administrator).validate must beLeft
        }
      }
    }
  }
}
