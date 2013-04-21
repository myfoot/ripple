package models.user

import models.CoreSchema
import org.squeryl._
import PrimitiveTypeMode._
import models.social.SocialUser
import models.util.ValidationTypes._
import models.exception.ValidationError

object UserRepository {
  def find(name:String, password:String):Option[User] = inTransaction{
    CoreSchema.users
      .where(user => user.name === name)
      .where(user => user.password === password).headOption
  }

  def findById(id:Long):Option[User] = inTransaction{
    CoreSchema.users.lookup(id)
  }

  def insert(user:User) = {
    user.validate match {
      case result@Right(_) => {
        inTransaction{ CoreSchema.users.insert(user) }
        result
      }
      case x => x
    }
  }

  def insertAsSocialUser(socialUser: SocialUser, token: String, secret: String): Either[Error, (User, AccessToken)] = {
    try {
      inTransaction {
        insert(User(socialUser.name, "", "default-password", LoggedInUser)) match {
          case Right(user) => {
            AccessTokenRepository.insert(AccessToken(socialUser.provider, token, secret, user.id)) match {
              case Right(accessToken) => Right(user, accessToken)
              case Left(error) => throw new ValidationError(error)
            }
          }
          case Left(error) => throw new ValidationError(error)
        }
      }
    } catch {
      case e: ValidationError => Left(e.errors)
    }
  }
}
