package com.logimethods.nats.connector.gatling

import java.util.Properties;
import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import io.gatling.core.config.GatlingConfiguration
import akka.actor.ActorSystem
import io.gatling.core.CoreComponents

import io.gatling.core.protocol.{ Protocol, ProtocolKey }

object NatsProtocol {
  
  val NatsProtocolKey = new ProtocolKey {
    
    type Protocol = NatsProtocol
    type Components = NatsComponents
    def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[NatsProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultValue(properties: Properties, subject: String): NatsProtocol = NatsProtocol(properties, subject)

/*    def newComponents(system: ActorSystem, coreComponents: CoreComponents): NatsProtocol => NatsComponents = {
      val natsEngine = NatsEngine(system, coreComponents)

      natsProtocol => {
        val natsComponents = NatsComponents(
          natsProtocol,
          natsEngine,
          new NatsCaches(coreComponents.configuration),
          new ResponseProcessor(coreComponents.statsEngine, natsEngine, coreComponents.configuration)(system)
        )

        natsEngine.warmpUp(natsComponents)
        natsComponents
      }  */
  }
}

case class NatsProtocol(properties: Properties, subject: String) extends Protocol {
  
  type Components = NatsComponents
  
  var connection: Connection = null
  
  override def warmUp(): Unit = {
    val connectionFactory: ConnectionFactory = new ConnectionFactory(properties);
    connection = connectionFactory.createConnection()
  }
}
