package models

import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.{Result, AsResult}
import org.specs2.mutable.Around

trait WithPlayContext extends Around {
  def before = {}
  def after = {}
  def around[T](spec: => T)(implicit evidence$1: AsResult[T]): Result = AsResult{
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      before
      val result = spec
      after
      result
    }
  }
}
