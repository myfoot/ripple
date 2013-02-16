package models.util

import sun.swing.plaf.synth.Paint9Painter
import annotation.tailrec

case class ValidatorComposite(validators: Seq[Validator]) extends Validator {
  val continue: Boolean = true

  def validate: Either[ValidatorTypes.ErrorNames, Any] = {
    _validate(validators.head, validators.tail, List[Symbol]()) match {
      case x if x.isEmpty => Validator.right
      case x => Left(x)
    }
  }

  @tailrec
  private def _validate(head: Validator, tail: Seq[Validator], errors: ValidatorTypes.ErrorNames): ValidatorTypes.ErrorNames = {
    (head.validate, head.continue) match {
      case (Left(x), false)  => errors ::: x
      case (Left(x), true) if tail.isEmpty => errors ::: x
      case (Right(_), _) if tail.isEmpty => errors
      case (result, _) => _validate(tail.head, tail.tail, result match {
        case Left(x) => errors ::: x
        case Right(_) => errors
      })
    }
  }

  def :+(validator: Validator) = {
    new ValidatorComposite(validators :+ validator)
  }
}

object ValidatorComposite {
  implicit def convertToComposite(validator: Validator) = new ValidatorComposite(Seq(validator))
}
