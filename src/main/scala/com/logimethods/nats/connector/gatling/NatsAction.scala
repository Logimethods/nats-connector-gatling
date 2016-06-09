/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

// @see https://www.trivento.io/write-custom-protocol-for-gatling/
package com.logimethods.nats.connector.gatling

import akka.actor.ActorDSL._
import akka.actor.ActorRef
import io.gatling.core.action.Chainable
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.{Protocol, Protocols}
import io.gatling.core.result.message.{KO, OK}
import io.gatling.core.result.writer.DataWriterClient
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper
import java.util.Properties;
import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import io.nats.client.Message;

/** A Gatling Protocol to inject messages into NATS.
 *  
 * @see [[https://www.trivento.io/write-custom-protocol-for-gatling/ Write a Custom Protocol for Gatling]] 
 * @see [[https://github.com/nats-io/jnats/blob/jnats-0.4.1/src/main/java/io/nats/client/ConnectionFactory.java ConnectionFactory.java]]
 * @see [[http://nats-io.github.io/jnats/io/nats/client/ConnectionFactory.html ConnectionFactory API]]
 * 
 * @constructor create a new Protocol defined by connection to a NATS server and a subject.
 * @param properties defining the parameters of NATS server to connect to. This connection is provided by a `new ConnectionFactory(properties)`
 * @param subject the subject on which the messages will be pushed to NATS
 */
case class NatsProtocol(properties: Properties, subject: String) extends Protocol {
  var connection: Connection = null
  
  override def warmUp(): Unit = {
    val connectionFactory: ConnectionFactory = new ConnectionFactory(properties);
    connection = connectionFactory.createConnection()
  }
}

/** A Gatling ActionBuilder to inject messages into NATS.
 * 
 * Possible usage:
 * {{{
 *     val natsScn = scenario("NATS call").exec(NatsBuilder(new ValueProvider()))
 * }}}
 * {{{
 * class ValueProvider {
 *   val incr = 10
 *   val basedValue = 100 -incr
 *   val maxIncr = 50
 *   var actualIncr = 0
 *   
 *   override def toString(): String = {
 *     actualIncr = (actualIncr % (maxIncr + incr)) + incr
 *     (basedValue + actualIncr).toString()
 *   }
 * }
 * }}}
 *  
 * @see [[https://www.trivento.io/write-custom-protocol-for-gatling/ Write a Custom Protocol for Gatling]] 
 * @constructor create a new NatsBuilder that will emit messages into NATS.
 * @param messageProvider  the provider of the messages to emit. The actual message will be the output of the toString() method applied to this object
 * (which could be a simple String if the message doesn't have to change over time). 
 */
case class NatsBuilder(messageProvider: Object) extends ActionBuilder {
  def natsProtocol(protocols: Protocols) =
    protocols.getProtocol[NatsProtocol]
      .getOrElse(throw new UnsupportedOperationException("NatsProtocol Protocol wasn't registered"))

  protected class NatsCall(messageProvider: Object, protocol: NatsProtocol, val next: ActorRef) extends Chainable with DataWriterClient {
    override def execute(session: Session): Unit = {
      protocol.connection.publish(protocol.subject, messageProvider.toString().getBytes())
      
      next ! session
    }
  } 
  
  override def build(next: ActorRef, protocols: Protocols): ActorRef = {
    actor(actorName("NatsConnector")) {
      new NatsCall(messageProvider, natsProtocol(protocols), next)
    }
  }
}
