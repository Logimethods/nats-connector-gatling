package com.logimethods.connector.gatling.to_nats

abstract trait NatsMessage {
  def getSubject(): String
  def getPayload(): Array[Byte]
}