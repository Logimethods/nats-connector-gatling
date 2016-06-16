name := "nats-connector-gatling"

organization := "com.logimethods"

version := "0.1.0"

scalaVersion := "2.11.7"

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "provided"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test"
libraryDependencies += "io.nats" 			   % "jnats"					 % "0.4.1"

// enablePlugins(GatlingPlugin)

updateOptions := updateOptions.value.withCachedResolution(true)

// PUBLISH
// See http://www.scala-sbt.org/0.13.5/docs/Detailed-Topics/Publishing.html

val SONATYPE_USERNAME = scala.util.Properties.envOrElse("SONATYPE_USERNAME", "NOT_SET")
val SONATYPE_PASSWORD = scala.util.Properties.envOrElse("SONATYPE_PASSWORD", "NOT_SET")
credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", SONATYPE_USERNAME, SONATYPE_PASSWORD)

licenses := Seq("MIT License" -> url("https://github.com/Logimethods/nats-connector-gatling/blob/master/LICENSE"))
homepage := Some(url("https://github.com/Logimethods/nats-connector-gatling"))
scmInfo := Some(ScmInfo(url("https://github.com/Logimethods/nats-connector-gatling"), "scm:git:git://github.com:Logimethods/nats-connector-gatling.git"))

publishMavenStyle := true
publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}