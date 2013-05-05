import play.Project._
import sbt._

object Dependencies {
  val resolvers = Seq(
    "t2v.jp repo" at "http://www.t2v.jp/maven-repo/",
    "twitter4j repo" at "http://twitter4j.org/maven2",
    "Twitter repo" at "http://maven.twttr.com/",
    "Sedis repo" at "http://pk11-scratch.googlecode.com/svn/trunk"
  )

  val dependencies = Seq(
    jdbc,
    anorm,
    "org.squeryl" %% "squeryl" % "0.9.5-6" withSources(),
    "org.sedis" % "sedis_2.10.0" % "1.1.8",
    "mysql" % "mysql-connector-java" % "5.1.21",
    "jp.t2v" %% "play21.auth" % "0.7" withSources(),
    "org.twitter4j" % "twitter4j-core" % "[3.0,)",
    "com.jsuereth" %% "scala-arm" % "1.3",
    "com.twitter" % "util-eval" % "6.1.0" withSources(),
    "org" % "jaudiotagger" % "2.0.3" withSources(),
    "com.typesafe" % "config" % "1.0.0",
    "org.mockito" % "mockito-all" % "1.9.0" % "test"
  )
}