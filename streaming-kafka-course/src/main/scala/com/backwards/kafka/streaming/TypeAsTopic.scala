package com.backwards.kafka.streaming

import com.backwards.kafka.streaming.StringOps.toLowerKebab

trait TypeAsTopic {
  val topic: String =
    toLowerKebab(getClass.getSimpleName.replaceAll("\\$", ""))
}