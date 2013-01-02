package models

import org.specs2.mutable._
import models.CoreSchema._
import org.squeryl.adapters.H2Adapter
import org.squeryl._
import PrimitiveTypeMode._
import scala.Some
import org.specs2.mock._

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/08/31
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
class SpecBase extends Specification with Before with Mockito {
  def before = initialDB

  def initialDB = {
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
}
