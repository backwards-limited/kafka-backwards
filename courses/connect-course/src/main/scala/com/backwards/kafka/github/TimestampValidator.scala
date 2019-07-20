package com.backwards.kafka.github

import java.time.Instant
import java.time.format.DateTimeParseException
import org.apache.kafka.common.config.ConfigDef.Validator
import org.apache.kafka.common.config.ConfigException

class TimestampValidator extends Validator {
  def ensureValid(name: String, value: AnyRef): Unit = {
    val timestamp = value.asInstanceOf[String]

    try {
      Instant.parse(timestamp)
    } catch {
      case e: DateTimeParseException =>
        throw new ConfigException(name, value, "Wasn't able to parse the timestamp, make sure it is formatted according to ISO-8601 standards")
    }
  }
}