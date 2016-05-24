/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

// @see https://www.trivento.io/write-custom-protocol-for-gatling/
package com.logimethods.connector.nats.gatling

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

case class NatsProtocol(properties: Properties, subject: String) extends Protocol {
  var connection: Connection = null
  
  override def warmUp(): Unit = {
    val connectionFactory: ConnectionFactory = new ConnectionFactory(properties);
    connection = connectionFactory.createConnection()
  }
}

case class NatsBuilder(message: String) extends ActionBuilder {
  def natsProtocol(protocols: Protocols) =
    protocols.getProtocol[NatsProtocol]
      .getOrElse(throw new UnsupportedOperationException("NatsProtocol Protocol wasn't registered"))
 
  override def build(next: ActorRef, protocols: Protocols): ActorRef = {
    actor(actorName("NatsConnector")) {
      new NatsCall(message, natsProtocol(protocols), next)
    }
  }
}

class NatsCall(message: String, protocol: NatsProtocol, val next: ActorRef) extends Chainable with DataWriterClient {
  override def execute(session: Session): Unit = {
    val natsMessage  = new Message()
    val payload = message.getBytes()
    natsMessage.setData(payload, 0, payload.length)
    protocol.connection.publish(natsMessage)
    
    next ! session
  }
}