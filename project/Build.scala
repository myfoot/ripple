import sbt._
import org.scalastyle.sbt.ScalastylePlugin
import Keys._

object ApplicationBuild extends Build {

  val appName         = "ripple"
  val appVersion      = "1.0"

  lazy val appSettings = Defaults.defaultSettings ++
                         ScalastylePlugin.Settings ++
                         Seq(ScctPlugin.instrumentSettings: _*)

  lazy val main = play.Project(appName, appVersion, Dependencies.dependencies, settings = appSettings).settings(
    scalaVersion := "2.10.0",
    resolvers ++= Dependencies.resolvers,
    testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "console")
  )
}