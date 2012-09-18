package models.user

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/10/16
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
sealed trait Role {
  val name:String
}
object Role {
  private val roles:Seq[Role] = Seq(Administrator,LoggedInUser,GuestUser)
  def get(name:String):Role = roles.find(_.name == name).getOrElse(GuestUser)
}
case object Administrator extends Role {
  val name:String = "administrator"
}
case object LoggedInUser extends Role {
  val name:String = "logged_in_user"
}
case object GuestUser extends Role {
  val name:String = "guest_user"
}

