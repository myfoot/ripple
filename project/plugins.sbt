// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// coverage plugin repository
resolvers += "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % Option(System.getProperty("play.version")).getOrElse("2.1.0"))

// Use scct for coverage
addSbtPlugin("reaktor" % "sbt-scct" % "0.2-SNAPSHOT")

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.18"
