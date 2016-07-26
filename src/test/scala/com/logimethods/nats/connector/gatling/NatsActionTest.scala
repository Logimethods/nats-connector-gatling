/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package com.logimethods.nats.connector.gatling

import akka.actor.{ActorRef, Props}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder

import scala.concurrent.duration._
import java.util.Properties

class NatsActionTest extends Simulation {
  
  val properties = new Properties()
  val natsProtocol = NatsProtocol(properties, "TestingSubject")
  
  val natsScn = scenario("NATS call").exec(NatsBuilder("Hello from Gatling!"))
 
  setUp(
    natsScn.inject(constantUsersPerSec(15) during (1 minute))
  ).protocols(natsProtocol)
}