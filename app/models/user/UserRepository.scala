package models.user

import models.CoreSchema
import org.squeryl._
import PrimitiveTypeMode._

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/08/31
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
object UserRepository {
  def find(name:String, password:String):Option[User] = inTransaction{
    CoreSchema.users
      .where(user => user.name === name)
      .where(user => user.password === password).toList.headOption
  }
}
