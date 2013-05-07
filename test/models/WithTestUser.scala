package models

import org.squeryl.PrimitiveTypeMode._
import models.CoreSchema._
import models.user.{User, LoggedInUser}

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 2013/05/07
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
trait WithTestUser {
  lazy val user = User("test-user", "hoge@gmail.com", "pass", LoggedInUser)
  def saveUser = user.save
  def cleanUser = users.deleteWhere(u => u.id === user.id)
}
