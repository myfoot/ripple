package models

import org.specs2.mutable._
import org.specs2.mock._

class ModelSpecBase extends Specification with Mockito {

  def verifyRequiredText[A <: BaseEntity](f: String => A) = {
    new RequiredTextTests[A] {
      def model(value: String): A = f(value)
    }.define
  }
  private trait RequiredTextTests[A <: BaseEntity] {
    def model(value:String): A

    def define = {
      "空文字は不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model("")
        expectFailed()
      }
      "半角スペースのみは不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model(" ")
        expectFailed()
      }
      "全角スペースのみは不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model("　")
        expectFailed()
      }
      "半角スペース&全角スペースのみは不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model(" 　　 ")
        expectFailed()
      }
    }
  }

  trait ValidationTest[A <: BaseEntity] {
    val target: A

    def expectFailed(expect: Option[Error] = None) = {
      expect
        .map{ error => target.validate must beLeft(error)}
        .getOrElse{ target.validate must beLeft }
    }
    def expectSuccess(expect: A = target) = {
      target.validate must beRight(expect)
    }
  }
}
