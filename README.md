# NATS Gatling Connector
The NATS Gatling connector provides a [Gatling](http://gatling.io/) (an open-source load testing framework based on Scala, Akka and Netty) to [NATS messaging system](https://nats.io) (a highly performant cloud native messaging system) Connector.

[![License MIT](https://img.shields.io/npm/l/express.svg)](http://opensource.org/licenses/MIT)
[![wercker status](https://app.wercker.com/status/e6e3cb5b6076bbd732a840a2802a18da/m/master "wercker status")](https://app.wercker.com/project/bykey/e6e3cb5b6076bbd732a840a2802a18da)

## Summary


## Installation

### Maven Central

#### Releases

The NATS Redis connector is currently alpha and there are no official releases.

#### Snapshots

Snapshots are regularly uploaded to the Sonatype OSSRH (OSS Repository Hosting) using
the same Maven coordinates.
If you are embedding the NATS Redis connector, add the following dependency to your project's `pom.xml`.

```xml
  <dependencies>
    ...
    <dependency>
      <groupId>com.logimethods</groupId>
      <artifactId>nats-connector-gatling_2.11</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
```
If you don't already have your pom.xml configured for using Maven snapshots, you'll also need to add the following repository to your pom.xml.

```xml
<repositories>
    ...
    <repository>
        <id>sonatype-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```
