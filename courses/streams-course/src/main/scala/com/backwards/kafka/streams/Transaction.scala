package com.backwards.kafka.streams

import java.time.LocalDateTime

final case class Transaction(name: String, amount: Int, time: LocalDateTime)