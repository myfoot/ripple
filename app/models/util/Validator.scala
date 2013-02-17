package models.util

trait Validator {
  def validate: Either[ValidatorTypes.ErrorNames, Any]
  val continue: Boolean
}

object Validator {
  val right = Right(true)
}

object ValidatorTypes {
  type ErrorNames = List[Symbol]
}

