package models.util

import UniqueValidator._

class UniqueValidator[A](validateFunction: => Option[A], val continue: Boolean = true) extends Validator {
  def validate: Either[ValidatorTypes.ErrorNames, Any] = validateFunction match {
    case Some(_) => Left(List(KEY))
    case _ => Validator.right
  }
}

object UniqueValidator {
  val KEY = 'unique
}
