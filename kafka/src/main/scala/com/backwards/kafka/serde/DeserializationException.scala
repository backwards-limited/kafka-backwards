package com.backwards.kafka.serde

case class DeserializationException(cause: Throwable) extends Exception(cause)