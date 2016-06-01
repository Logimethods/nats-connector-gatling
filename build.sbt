name := "nats-connector-gatling"

organization := "com.logimethods"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "provided"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test"
libraryDependencies += "io.nats" 			   % "jnats"					 % "0.4.1"

// enablePlugins(GatlingPlugin)

updateOptions := updateOptions.value.withCachedResolution(true)

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

val SONATYPE_USERNAME = scala.util.Properties.envOrElse("SONATYPE_USERNAME", "NOT_SET")
val SONATYPE_PASSWORD = scala.util.Properties.envOrElse("SONATYPE_PASSWORD", "NOT_SET")
credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.scala-tools.org", SONATYPE_USERNAME, SONATYPE_PASSWORD)