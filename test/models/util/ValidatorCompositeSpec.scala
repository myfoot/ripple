package models.util

import org.specs2.mutable.{Before, Specification}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

class ValidatorCompositeSpec extends Specification with Mockito {
  "#validate" should {
    "continue true" in {
      "保持している全Validatorのvalidateを実行する" in {
        val validator1 = mockValidator("v1")
        validator1.validate returns Left(List('hoge))
        val validator2 = mockValidator("v2")
        validator2.validate returns Left(List('foo))

        new ValidatorComposite(Seq(validator1, validator2)).validate must beLeft(List('hoge, 'foo))
      }
    }
    "continue false" in {
      "validateが失敗した場合、以降のvalidateを行わない" in {
        val validator1 = mockValidator("v1", false)
        validator1.validate returns Left(List('hoge))
        val validator2 = mockValidator("v2")
        validator2.validate returns Left(List('foo))

        new ValidatorComposite(Seq(validator1, validator2)).validate must beLeft(List('hoge))
        there was no(validator2).validate
      }
      "validateが成功した場合、以降のvalidateも行う" in {
        val validator1 = mockValidator("v1", false)
        validator1.validate returns Validator.right
        val validator2 = mockValidator("v2")
        validator2.validate returns Left(List('foo))

        new ValidatorComposite(Seq(validator1, validator2)).validate must beLeft(List('foo))
      }
    }
  }

  "#:+" should {
    "指定されたvalidatorを追加したコンポジットを返す" in {
      val validator1 = mockValidator("v1")
      val validator2 = mockValidator("v2")
      val newComposite = new ValidatorComposite(Seq(validator1)) :+ validator2
      newComposite.validators must be_==(Seq(validator1, validator2))
    }
  }

  "impliit .convertToComposite" should {
    "Validatorオブジェクト同士を結合させた場合、Compositeになる" in {
      import ValidatorComposite._
      val v1 = mockValidator("v1")
      val v2 = mockValidator("v2")
      val composite = v1 :+ v2
      composite must beAnInstanceOf[ValidatorComposite]
      composite.validators must be_==(Seq(v1, v2))
    }
  }

  def mockValidator(name: String = "v1", continue: Boolean = true) = {
    val validator = mockAs[Validator](name)
    validator.continue returns continue
    validator
  }

}
