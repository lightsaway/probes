package lightsaway.probes

import cats.{Applicative, Monad, Parallel}
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext
import cats.Parallel.parSequence
import cats.effect.Sync
import fs2.Stream
import lightsaway.probes.ProbeStore.ProbeStore

object ProbesStream {
  def apply[F[_]: Parallel](
      probes: List[Probe[F]]
  )(implicit EC: ExecutionContext): Stream[F, List[ProbeStatus[F]]] =
    Stream.eval(parSequence(probes.map(_.status())))
}

object ProbesSink {
  def apply[F[_]: Applicative](
      store: ProbeStore[F]
  ): List[ProbeStatus[F]] => F[Unit] =
    _.traverse_(status => store.modify(c => (c + (status.probe -> status), c)))
}

object ProbeStore {
  type ProbeStore[F[_]] = Ref[F, HashMap[Probe[F], ProbeStatus[F]]]
  def apply[F[_]: Sync](
      initial: HashMap[Probe[F], ProbeStatus[F]] =
        HashMap.empty[Probe[F], ProbeStatus[F]]): F[ProbeStore[F]] =
    Ref.of[F, HashMap[Probe[F], ProbeStatus[F]]](initial)
}
