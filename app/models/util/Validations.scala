package models.util

trait Validations {
  type ModelClass <: Validations
  lazy val validators: Map[Symbol, Validator] = Map.empty

  def validate: Either[Map[Symbol, ValidatorTypes.ErrorNames], ModelClass] = {
    validators.foldLeft(Map[Symbol, ValidatorTypes.ErrorNames]()){ case (acc, (fieldName, validator)) =>
      validator.validate match {
        case Right(_) => acc
        case Left(x) => acc + (fieldName -> x)
      }
    } match {
      case x if x.isEmpty => Right(this.asInstanceOf[ModelClass])
      case x => Left(x)
    }
  }
}

object Validations {
  def unique[A](searchFunction: Option[A], continue: Boolean = true) = new UniqueValidator[A](searchFunction, continue)
  def requiredText(value: String, continue: Boolean = true) = new RequireTextValidator(value, continue)
}