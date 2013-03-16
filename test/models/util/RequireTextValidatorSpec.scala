package models.util

import org.specs2.mutable.Specification

class RequireTextValidatorSpec extends Specification {
  "validate" should {
    "空文字列の場合はエラー" >> {
      new RequireTextValidator("").validate must beLeft(List(RequireTextValidator.KEY))
    }
    "空文字列文字列でない場合は成功（半角・全角空白も許容する）" >> {
      new RequireTextValidator(" ").validate must beRight
      new RequireTextValidator("　").validate must beRight
      new RequireTextValidator("hoge").validate must beRight
    }
  }
}
