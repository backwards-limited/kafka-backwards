package com.backwards.kafka.producer

import java.time.Instant
import eu.timepit.refined.api.RefType.refinedRefType
import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.AutoDerivation
import io.circe.{Decoder, Encoder}
import com.backwards.kafka.producer.Invoice.{InvoiceId, InvoiceNumber, InvoiceStoreId}
//import com.backwards.kafka.producer.Invoice._
import io.circe.generic.semiauto._
import shapeless.Unwrapped

final case class Invoice(id: InvoiceId, number: InvoiceNumber, created: Instant, storeId: InvoiceStoreId)

object Invoice /*extends ValueClassCodec with AutoDerivation*/ {
  /*type Id = NonEmptyString
  object Id extends RefinedTypeOps[Id, String]*/

  /*type InvoiceNumber = NonEmptyString
  object InvoiceNumber extends RefinedTypeOps[InvoiceNumber, String]*/

  final case class InvoiceId(value: String) extends AnyVal

  final case class InvoiceNumber(value: String) extends AnyVal

  /*object InvoiceNumber {
    import io.circe.generic.extras.semiauto._

    implicit val xxx: Encoder[InvoiceNumber] = deriveUnwrappedEncoder
  }*/

  /*type StoreId = NonEmptyString
  object StoreId extends RefinedTypeOps[StoreId, String]*/


  final case class InvoiceStoreId(value: String) extends AnyVal
}