package com.backwards.transform

import scala.concurrent.Future
import scala.language.higherKinds
import cats.effect.IO
import cats.~>

object Transform extends Transform

trait Transform {
  implicit class LiftTo[X[_], A](x: X[A]) {
    def liftTo[Y[_]](implicit transform: X ~> Y): Y[A] = transform(x)
  }

  implicit val `future ~> IO`: ~>[Future, IO] = new (Future ~> IO) {
    override def apply[A](future: Future[A]): IO[A] =
      IO fromFuture IO(future)
  }
}