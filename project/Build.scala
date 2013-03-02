import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "ripple"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      jdbc,
      anorm,
      "org.squeryl" %% "squeryl" % "0.9.5-6" withSources(),
      "mysql" % "mysql-connector-java" % "5.1.21",
      "jp.t2v" %% "play21.auth" % "0.7" withSources(),
      "org.twitter4j" % "twitter4j-core" % "[3.0,)",
      "com.twitter" % "util-eval" % "6.1.0" withSources(),
      "org.mockito" % "mockito-all" % "1.9.0" % "test"
    )

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.0",
    resolvers ++= Seq(
      "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
      "twitter4j repo" at "http://twitter4j.org/maven2",
      "Twitter repo" at "http://maven.twttr.com/"
    ),
    Tasks.seedTask
  )

  object Tasks {
    val seedDataKey = TaskKey[Unit]("seed", "insert seed data")
    lazy val seedTask = seedDataKey := {
      // 2.1からモジュールが分割された為か、play.api系が使えないので、一旦コメントアウト（ユーザー登録機能を実装すれば不要なので）
//      TestData.insert
    }
  }
}

//sealed trait WithApplication {
//  def startApp = {
//    Play.start(new Application(new java.io.File("."), this.getClass.getClassLoader, None, Mode.Dev))
//  }
//}
//
//object TestData extends WithApplication {
//  def insert = {
//    startApp
//    import play.api.db._
//    import anorm._
//    import play.api.Play.current
//    DB.withConnection { implicit connection =>
//      SQL("truncate user").execute
//      SQL(
//        """
//          insert into user(id, name, email, password, roleName)
//          values ({id}, {name}, {email}, {password}, {roleName})
//        """
//      ).on(
//        'id -> 1,
//        'name -> "test-user",
//        'email -> "hoge@gmail.com",
//        'password -> "pass",
//        'roleName -> "administrator"
//      ).executeUpdate()
//    }
//  }
//}
