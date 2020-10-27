package com.backwards.kafka.producer

import io.circe.{Decoder, Encoder}
import shapeless.Unwrapped

// TODO - Move
object ValueClassCodec extends ValueClassCodec

trait ValueClassCodec {
  implicit def decodeAnyVal[T, U](
    implicit ev: T <:< AnyVal,
    unwrapped: Unwrapped.Aux[T, U],
    decoder: Decoder[U]
  ): Decoder[T] = Decoder.instance[T] { cursor =>
    decoder(cursor).map(value => unwrapped.wrap(value))
  }

  implicit def encodeAnyVal[T, U](
    implicit ev: T <:< AnyVal,
    unwrapped: Unwrapped.Aux[T, U],
    encoder: Encoder[U]
  ): Encoder[T] = Encoder.instance[T] { value =>
    encoder(unwrapped.unwrap(value))
  }
}