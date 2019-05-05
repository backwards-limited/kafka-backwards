package com.backwards.kafka.streaming

import com.backwards.kafka.streaming.StringOps.toLowerKebab

trait TypeAsClientId {
  val clientId: String =
    toLowerKebab(getClass.getSimpleName.replaceAll("\\$", ""))
}