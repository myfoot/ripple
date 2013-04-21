package models.user

import org.specs2.mutable.Specification

/**
 * Created with IntelliJ IDEA.
 * User: natsuki
 * Date: 12/10/16
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
class RoleSpec extends Specification {
  "#get" should {
    "指定したRole名と一致するRoleがある場合はRoleが取得できる" >> {
      Role.get(Administrator.name) must equalTo(Administrator)
      Role.get(LoggedInUser.name) must equalTo(LoggedInUser)
      Role.get(GuestUser.name) must equalTo(GuestUser)
    }
    "指定したRole名と一致するRoleがない場合はGuestUserが取得できる" >> {
      Role.get("hoge") must equalTo(GuestUser)
    }
  }
}
