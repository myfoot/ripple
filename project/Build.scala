import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ripple"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.squeryl" %% "squeryl" % "0.9.5" withSources(),
      "mysql" % "mysql-connector-java" % "5.1.18",
      "jp.t2v" % "play20.auth_2.9.1" % "0.3" withSources()
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
    )

}
