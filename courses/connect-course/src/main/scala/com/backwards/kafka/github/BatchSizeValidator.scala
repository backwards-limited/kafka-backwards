package com.backwards.kafka.github

import org.apache.kafka.common.config.ConfigDef.Validator
import org.apache.kafka.common.config.ConfigException

class BatchSizeValidator extends Validator {
  def ensureValid(name: String, value: AnyRef): Unit = {
    val batchSize = value.asInstanceOf[Int]

    if (!(1 <= batchSize && batchSize <= 100))
      throw new ConfigException(name, value, "Batch Size must be a positive integer that's less or equal to 100")
  }
}