import play.api.{Play, Mode, Application}
import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ripple"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.squeryl" %% "squeryl" % "0.9.5-2" withSources(),
      "mysql" % "mysql-connector-java" % "5.1.21",
      "jp.t2v" % "play20.auth_2.9.1" % "0.3" withSources()
    )

  lazy val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
    Tasks.seedTask
  )

  object Tasks {
    val seedDataKey = TaskKey[Unit]("seed", "insert seed data")
    lazy val seedTask = seedDataKey := {
      TestData.insert
    }
  }
}

sealed trait WithApplication {
  def startApp = {
    Play.start(new Application(new java.io.File("."), this.getClass.getClassLoader, None, Mode.Dev))
  }
}

object TestData extends WithApplication {
  def insert = {
    startApp
    import play.api.db._
    import anorm._
    import play.api.Play.current
    DB.withConnection { implicit connection =>
      SQL("truncate user").execute
      SQL(
        """
          insert into user(id, name, email, password, roleName)
          values ({id}, {name}, {email}, {password}, {roleName})
        """
      ).on(
        'id -> 1,
        'name -> "test-user",
        'email -> "hoge@gmail.com",
        'password -> "pass",
        'roleName -> "administrator"
      ).executeUpdate()
    }
  }
}
