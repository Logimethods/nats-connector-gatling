/*******************************************************************************
 * Copyright (c) 2016 Logimethods
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

// @see https://www.trivento.io/write-custom-protocol-for-gatling/
package com.logimethods.connector.gatling.to_nats

import akka.actor.{ ActorSystem, Props }
import io.gatling.commons.stats.{ KO, OK }
import io.gatling.commons.util.TimeHelper
import io.gatling.core.CoreComponents
import io.gatling.commons.stats.{ KO, OK }
import io.gatling.commons.util.TimeHelper
import io.gatling.core.CoreComponents
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.action.{ Action, ActionActor, ExitableActorDelegatingAction }
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol._
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext
import io.gatling.jms.action.JmsReqReply._

import scala.concurrent.{ Future, Promise }

import java.util.Properties;
import io.nats.stan.Connection;
import io.nats.stan.ConnectionFactory;

import com.typesafe.scalalogging.StrictLogging

object NatsStreamingProtocol {
  val NatsStreamingProtocolKey = new ProtocolKey {

    type Protocol = NatsStreamingProtocol
    type Components = NatsStreamingComponents

    def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[NatsStreamingProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultValue(configuration: GatlingConfiguration): NatsStreamingProtocol = throw new IllegalStateException("Can't provide a default value for NatsStreamingProtocol")

    def newComponents(system: ActorSystem, coreComponents: CoreComponents): NatsStreamingProtocol ⇒ NatsStreamingComponents = {
      natsProtocol ⇒ NatsStreamingComponents(natsProtocol)
    }
  }
}

/** A Gatling Protocol to inject messages into NATS Streaming.
 *  
 * @see [[https://www.trivento.io/write-custom-protocol-for-gatling/ Write a Custom Protocol for Gatling]] 
 * @see [[https://github.com/nats-io/jnats/blob/jnats-0.4.1/src/main/java/io/nats/client/ConnectionFactory.java ConnectionFactory.java]]
 * @see [[http://nats-io.github.io/jnats/io/nats/client/ConnectionFactory.html ConnectionFactory API]]
 * 
 * @constructor create a new Protocol defined by connection to a NATS server and a subject.
 * @param properties defining the parameters of NATS server to connect to. This connection is provided by a `new ConnectionFactory(properties)`
 * @param subject the subject on which the messages will be pushed to NATS
 */
case class NatsStreamingProtocol(natsUrl:String, clusterID: String, subject: String, serializer: Object => Array[Byte] = (_.toString().getBytes()) )
                extends Protocol with StrictLogging {
    val CLIENT_ID_ROOT = "NSP_"
    val clientID = CLIENT_ID_ROOT + System.identityHashCode(this) + Thread.currentThread().getId() + java.lang.System.currentTimeMillis()
    val connectionFactory: ConnectionFactory = new ConnectionFactory(clusterID, clientID)
    connectionFactory.setNatsUrl(natsUrl)
    val connection: Connection = connectionFactory.createConnection()
    
    logger.info(s"Connection to the '${clusterID}' NATS Streaming Server located at '${natsUrl}' with '$clientID' ClientID and '$subject' Subject")
}

case class NatsStreamingComponents(natsProtocol: NatsStreamingProtocol) extends ProtocolComponents {

  def onStart: Option[Session ⇒ Session] = None
  def onExit: Option[Session ⇒ Unit] = None
}

object NatsStreamingCall {
  def apply(messageProvider: Object, protocol: NatsStreamingProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action) = {
    val actor = system.actorOf(Props(new NatsStreamingCall(messageProvider, protocol, next, statsEngine)))
    new ExitableActorDelegatingAction(genName("natsCall"), statsEngine, next, actor)
  }

}

class NatsStreamingCall(messageProvider: Object, protocol: NatsStreamingProtocol, val next: Action, statsEngine: StatsEngine) extends ActionActor {

  override def execute(session: Session): Unit = {
    import com.logimethods.connector.gatling.to_nats.NatsMessage
    messageProvider match {
      case m: NatsMessage => protocol.connection.publish(protocol.subject + m.getSubject(), m.getPayload())
      case other => protocol.connection.publish(protocol.subject, protocol.serializer(messageProvider))
    }
    
    next ! session
  }
}

/** A Gatling ActionBuilder to inject messages into NATS.
 * 
 * Possible usage:
 * {{{
 *     val natsScn = scenario("NATS call").exec(NatsStreamingBuilder(new ValueProvider()))
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
 * @constructor create a new NatsStreamingBuilder that will emit messages into NATS.
 * @param messageProvider  the provider of the messages to emit. The actual message will be the output of the toString() method applied to this object
 * (which could be a simple String if the message doesn't have to change over time). 
 */
case class NatsStreamingBuilder(messageProvider: Object) extends ActionBuilder {
  def natsProtocol(protocols: Protocols) = protocols.protocol[NatsStreamingProtocol].getOrElse(throw new UnsupportedOperationException("NatsStreamingProtocol Protocol wasn't registered"))

  private def components(protocolComponentsRegistry: ProtocolComponentsRegistry): NatsStreamingComponents =
    protocolComponentsRegistry.components(NatsStreamingProtocol.NatsStreamingProtocolKey)

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx._
    val statsEngine = coreComponents.statsEngine

    val natsComponents = components(protocolComponentsRegistry)
    NatsStreamingCall(messageProvider, natsComponents.natsProtocol, ctx.system, statsEngine, next)
  }
}