package models.exception

import models.util.ValidationTypes._

case class ValidationError(errors: Error) extends RuntimeException
