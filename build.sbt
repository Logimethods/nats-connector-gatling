name := "nats-connector-gatling"

organization := "com.logimethods"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

/*
libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.2.1" % "provided",
  "io.nats" % "jnats" % "0.4.1" 
)*/

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % "2.1.7" % "test"
libraryDependencies += "io.nats" 			   % "jnats"					 % "0.4.1"

// enablePlugins(GatlingPlugin)

updateOptions := updateOptions.value.withCachedResolution(true)