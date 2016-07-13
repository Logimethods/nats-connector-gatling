# NATS Gatling Connector
The NATS Gatling library provides a [Gatling](http://gatling.io/) (an open-source load testing framework based on Scala, Akka and Netty) to [NATS messaging system](https://nats.io) (a highly performant cloud native messaging system) Connector.

[![License MIT](https://img.shields.io/npm/l/express.svg)](http://opensource.org/licenses/MIT)
[![wercker status](https://app.wercker.com/status/e6e3cb5b6076bbd732a840a2802a18da/s/master "wercker status")](https://app.wercker.com/project/bykey/e6e3cb5b6076bbd732a840a2802a18da)
[![Scaladoc](http://javadoc-badge.appspot.com/com.logimethods/nats-connector-spark.svg?label=scaladoc)](http://logimethods.github.io/nats-connector-gatling/)


## Summary


## Installation

### Maven Central

#### Releases

The first version (0.1.0) of the NATS Gatling connector has been released.
It hovewer probably still needs some more testing to be fully validated.

If you are embedding the NATS Gatling connector, add the following dependency to your project's `pom.xml`.

```xml
  <dependencies>
    ...
    <dependency>
      <groupId>com.logimethods</groupId>
      <artifactId>nats-connector-gatling_2.11</artifactId>
      <version>0.1.0</version>
    </dependency>
  </dependencies>
```
If you don't already have your pom.xml configured for using Maven releases from Sonatype / Nexus, you'll also need to add the following repository to your pom.xml.

```xml
<repositories>
    ...
    <repository>
        <id>sonatype-oss-public</id>
        <url>https://oss.sonatype.org/content/groups/public/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>
```

#### Snapshots

Snapshots are regularly uploaded to the Sonatype OSSRH (OSS Repository Hosting) using
the same Maven coordinates.
If you are embedding the NATS Gatling connector, add the following dependency to your project's `pom.xml`.

```xml
  <dependencies>
    ...
    <dependency>
      <groupId>com.logimethods</groupId>
      <artifactId>nats-connector-gatling_2.11</artifactId>
      <version>0.2.0-SNAPSHOT</version>
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
## Usage (in Scala), from Gatling to NATS
### Simple usage
```
...
import com.logimethods.nats.connector.gatling._

class NatsInjection extends Simulation {
  
  val properties = new Properties()
  properties.setProperty(io.nats.client.Constants.PROP_URL, "nats://localhost:4222")
  val natsProtocol = NatsProtocol(properties, "GatlingSubject") 
  val natsScn = scenario("NATS call").exec(NatsBuilder("Message from Gatling!"))
 
  setUp(
    natsScn.inject(constantUsersPerSec(15) during (1 minute))
  ).protocols(natsProtocol)
}
```
### More complex usage
```
import akka.actor.{ActorRef, Props}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.{Protocol, Protocols}
import com.logimethods.nats.connector.gatling._

import scala.concurrent.duration._
import java.util.Properties
import io.nats.client.Constants.PROP_URL

class NatsInjection extends Simulation {
  
  val properties = new Properties()
  // The URI of the NATS server is provided by an environment variable:
  // >export NATS_URI=nats://nats-main:4222
  println("System properties: " + System.getenv())
  val natsUrl = System.getenv("NATS_URI")
  properties.setProperty(io.nats.client.Constants.PROP_URL, natsUrl)
  // The NATS Subject is also provided by an environment variable:
  // >export GATLING.TO_NATS.SUBJECT=FROM_GATLING
  var subject = System.getenv("GATLING.TO_NATS.SUBJECT")
  if (subject == null) {
    println("No Subject has been defined through the 'GATLING.TO_NATS.SUBJECT' Environment Variable!!!")
  } else {
    println("Will emit messages to " + subject)
    val natsProtocol = NatsProtocol(properties, subject)
    
    // The messages sent to NATS will not be fixed thanks to the ValueProvider.
    val natsScn = scenario("NATS call").exec(NatsBuilder(new ValueProvider()))
   
    setUp(
      natsScn.inject(constantUsersPerSec(15) during (1 minute))
    ).protocols(natsProtocol)
  }
}

/**
 * The ValueProvider will generate a loop of values: 100, 110, 120, 130, 140, 150, 100...
 */
class ValueProvider {
  val incr = 10
  val basedValue = 100 -incr
  val maxIncr = 50
  var actualIncr = 0
  
  override def toString(): String = {
    actualIncr = (actualIncr % (maxIncr + incr)) + incr
    (basedValue + actualIncr).toString()
  }
}
```
## Samples
* The ['docker-nats-connector-spark'](https://github.com/Logimethods/docker-nats-connector-spark) Docker Based Project that makes use of Gatling, Spark & NATS.

## License

(The MIT License)

Copyright (c) 2016 Logimethods.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to
deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE SOFTWARE.
