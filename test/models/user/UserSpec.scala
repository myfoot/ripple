package models.user

import models.ModelSpecBase

class UserSpec extends ModelSpecBase {
  "User" should {
    "#validate" >> {
      "name" >> {
        "空文字不可" >> {
          verifyRequiredText(User(_, "email", "password", Administrator))
        }
      }
      "email" >> {
        "emailの形式以外は登録不可" >> {
          failure
        }.pendingUntilFixed("formatチェックはTODO")
      }
      "password" >> {
        "空文字不可" >> {
          verifyRequiredText(User("hoge", "email", _, Administrator))
        }
      }
    }
  }
}
