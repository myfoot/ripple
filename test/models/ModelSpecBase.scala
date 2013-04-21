package models

import org.specs2.mutable._
import org.specs2.mock._
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import scala.Some
import org.specs2.execute.{AsResult, Result}

class ModelSpecBase extends Specification with Mockito with Before {
  def before = initialDB

  private def initialDB = {
     println("Setting up data.")
     Class.forName("org.h2.Driver")
     SessionFactory.concreteFactory = Some(() =>
       Session.create(
         java.sql.DriverManager.getConnection("jdbc:h2:test/example", "sa", ""),
         new H2Adapter)
     )
     transaction {
       drop
       create
       //printDdl
     }
  }

  trait WithTransaction extends Around {
    def before = {}
    def after = {}
    def around[T](t: => T)(implicit evidence$1: AsResult[T]): Result = AsResult {
      transaction {
        before
        val result = t
        after
        result
      }
    }
  }

  def verifyRequiredText[A <: BaseEntity](f: String => A) = {
    new RequiredTextTests[A] {
      def model(value: String): A = f(value)
    }.define
  }

  private trait RequiredTextTests[A <: BaseEntity] {
    def model(value:String): A

    def define = {
      "空文字は不可" >> new ValidationTest[A] {
        val target: A = model("")
        expectFailed()
      }
      "半角スペースのみは不可" >> new ValidationTest[A] {
        val target: A = model(" ")
        expectFailed()
      }
      "全角スペースのみは不可" >> new ValidationTest[A] {
        val target: A = model("　")
        expectFailed()
      }
      "半角スペース&全角スペースのみは不可" >> new ValidationTest[A] {
        val target: A = model(" 　　 ")
        expectFailed()
      }
    }
  }

  trait ValidationTest[A <: BaseEntity] extends WithTransaction {
    val target: A

    def expectFailed(expect: Option[Error] = None): Result = {
      expect match {
        case Some(x) => target.validate must beLeft(x)
        case None => target.validate must beLeft
      }
    }
    def expectSuccess(expect: A = target): Result = {
      target.validate must beRight(expect)
    }
  }
}
