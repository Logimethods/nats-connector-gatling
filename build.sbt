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

pomExtra := (
  <url>http://www.logimethods.com</url>
  <issueManagement>
    <url>https://github.com/Logimethods/nats-connector-gatling/issues/</url>
    <system>GitHub Issues</system>
  </issueManagement>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git://github.com/Logimethods/nats-connector-gatling.git</connection>
    <developerConnection>scm:git:ssh://git@github.com/Logimethods/nats-connector-gatling.git</developerConnection>
    <url>http://github.com/Logimethods/nats-connector-gatling</url>
  </scm>
  <developers>
    <developer>
        <id>laugimethods</id>
        <name>Laurent Magnin</name>
        <email>laurent.magnin@logimethods.com</email>
        <url>https://github.com/laugimethods</url>
        <organization>Logimethods</organization>
        <organizationUrl>http://www.logimethods.com/</organizationUrl>
        <roles>
            <role>Senior Consultant</role>
        </roles>
        <timezone>America/Montreal</timezone>
    </developer>
  </developers>
)

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}