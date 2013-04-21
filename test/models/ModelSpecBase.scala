package models

import org.specs2.mutable._
import org.specs2.mock._
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import org.specs2.execute.{Result, AsResult}
import play.api.test.Helpers._
import scala.Some
import play.api.test.FakeApplication

class ModelSpecBase extends Specification with Mockito with Around {
  def before = {}
  def after = {}

  def around[T](spec: => T)(implicit evidence$1: AsResult[T]): Result = AsResult{
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      initialDB
      before
      val result = spec
      after
      result
    }
  }

  private def initialDB = {
     println("Setting up data.")
     Class.forName("org.h2.Driver")
     SessionFactory.concreteFactory = Some(() =>
       Session.create(
         java.sql.DriverManager.getConnection("jdbc:h2:test/example", "sa", ""),
         new H2Adapter)
     )
     inTransaction {
       drop
       create
       printDdl
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
      "空文字は不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model("")
        expectFailed()
      }
      "半角スペースのみは不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model(" ")
        expectFailed()
      }
      "全角スペースのみは不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model("　")
        expectFailed()
      }
      "半角スペース&全角スペースのみは不可" >> new WithPlayContext with ValidationTest[A] {
        val target: A = model(" 　　 ")
        expectFailed()
      }
    }
  }

  trait ValidationTest[A <: BaseEntity] {
    val target: A

    def expectFailed(expect: Option[Error] = None) = {
      expect
        .map{ error => target.validate must beLeft(error)}
        .getOrElse{ target.validate must beLeft }
    }
    def expectSuccess(expect: A = target) = {
      target.validate must beRight(expect)
    }
  }
}
