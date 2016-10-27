package com.logimethods.connector.gatling.to_nats

trait NatsMessage {
  def getSubject(): String
  def getPayload(): Array[Byte]
}