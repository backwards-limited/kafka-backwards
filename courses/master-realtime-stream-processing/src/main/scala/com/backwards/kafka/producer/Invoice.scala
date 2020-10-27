package com.backwards.kafka.producer

import java.time.Instant
import com.backwards.kafka.producer.Invoice._

final case class Invoice(id: InvoiceId, number: InvoiceNumber, created: Instant, storeId: InvoiceStoreId)

object Invoice {
  final case class InvoiceId(value: String) extends AnyVal

  final case class InvoiceNumber(value: String) extends AnyVal

  final case class InvoiceStoreId(value: String) extends AnyVal
}