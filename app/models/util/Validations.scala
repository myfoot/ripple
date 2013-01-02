package models.util

import scala._
import annotation.tailrec
import scala.Left
import scala.Some
import scala.Right
import scala.Predef._
import scala.Left
import scala.Some
import scala.Right
import anorm.SqlStatementParser

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2013/01/03
 * Time: 0:55
 * To change this template use File | Settings | File Templates.
 */
trait Validations {
  type ModelClass <: Validations

  val validations = scala.collection.mutable.Map[Symbol, Seq[(() => Boolean, String, Boolean)]]().empty
  protected def validation(property: Symbol, validation: () => Boolean, message: String, continue:Boolean = true) {
    validations.get(property) match {
      case Some(functions) => validations += property -> (functions :+ (validation, message, continue))
      case None => validations += property -> Seq((validation, message, continue))
    }
  }

  def validate: Either[ValidationTypes.Errors, ModelClass] = {
    validations.foldLeft(Map[Symbol, Seq[String]]()){ case (acc, (property, functions)) =>
      _validate(property, acc, functions.head, functions.tail)
    } match {
      case x if x.isEmpty => Right(this.asInstanceOf[ModelClass])
      case x@_ => Left(x)
    }

  }
  @tailrec
  private def _validate(property: Symbol, errors: ValidationTypes.Errors, validation: (() => Boolean, String, Boolean), others: Seq[(() => Boolean, String, Boolean)]): ValidationTypes.Errors = {
    val (function, message, continue) = validation
    val validationResult = function()
    val newErros = if (!validationResult) {
      errors + (property -> errors.get(property).map(_ ++ Seq(message)).getOrElse(Seq(message)))
    } else {
      errors
    }
    (continue, others) match {
      case (false, _) if !validationResult => newErros
      case (_, Seq()) => newErros
      case (_, x::xs) => _validate(property, newErros, x, xs)
    }
  }

  protected def unique[A](model: => Option[A]): () => Boolean = () => model match {
    case Some(x) => false
    case None => true
  }
  protected def notEmptyText(value: String): () => Boolean = () => !value.isEmpty
}

object ValidationTypes {
  type Errors = Map[Symbol, Seq[String]]
}
