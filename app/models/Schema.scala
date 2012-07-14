package models

import play.api.db._
import play.api.Play.current

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.{OneToMany}
import org.squeryl.Schema
import org.squeryl.annotations.Column
import org.squeryl.{Session, SessionFactory}

import java.sql.Timestamp
import System._
import java.lang.{Integer}
import user.User

/**
 * Created with IntelliJ IDEA.
 * User: yaginatsuki
 * Date: 12/04/29
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */

class BaseEntity extends KeyedEntity[Long] {
  val id: Long = 0
}

object CoreSchema extends Schema {
  val users = table[User]("user")

  on(users)(ent => declare(
    ent.id is(autoIncremented),
    ent.email is(unique, dbType("varchar(255)"))
  ))
}
