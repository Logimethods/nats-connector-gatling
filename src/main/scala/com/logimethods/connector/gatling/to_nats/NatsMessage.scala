package com.logimethods.connector.gatling.to_nats

sealed abstract trait NatsMessage {
  def getSubject(): String
  def getPayload(): Array[Byte]
}