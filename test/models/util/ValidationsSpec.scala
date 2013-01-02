package models.util

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

class ValidationsSpec extends Specification with Mockito {
  "#validate" should {
    lazy val model = new Validations {
      override lazy val validators: Map[Symbol, Validator] = Map(
        'hoge -> mockErrorValidator('unique),
        'foo  -> ValidatorComposite(Seq(mockErrorValidator('unique), mockErrorValidator('number))),
        'bar  -> mockSuccessValidator('xxx)
      )
    }
    "validatorsで定義されたvalidationを行い結果を返す" in {
      println(model.validate)
      model.validate must beLeft(Map('hoge -> List('unique), 'foo -> List('unique, 'number)))
    }
  }

  def mockErrorValidator(name: Symbol) = {
    val validator = mock[Validator].as(name.name)
    validator.validate returns Left(List(name))
    validator.continue returns true
    validator
  }

  def mockSuccessValidator(name: Symbol) = {
    val validator = mock[Validator].as(name.name)
    validator.validate returns Right(true)
    validator.continue returns true
    validator
  }

}
