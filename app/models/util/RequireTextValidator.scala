package models.util

import RequireTextValidator._

class RequireTextValidator(value: => String, val continue: Boolean = true) extends Validator {
  def validate: Either[ValidatorTypes.ErrorNames, Any] = if (value.nonEmpty) Validator.right else Left(List(KEY))
}

object RequireTextValidator {
  val KEY = 'requireText
}
