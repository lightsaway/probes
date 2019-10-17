package lightsaway.probes
import cats.effect.Effect
import cats.implicits._
sealed trait Severity
case object Warning extends Severity
case object Critical extends Severity

case class ProbeStatus[F[_]](probe: Probe[F], result: ProbeResult)
case class LabeledProbe[L, F[_]: Effect](label: L, probe: Probe[F])
    extends Probe[F] {
  override def evaluate(): F[ProbeResult] = probe.evaluate()
}

abstract class Probe[F[_]: Effect] {
  def evaluate(): F[ProbeResult]
  final def status(): F[ProbeStatus[F]] = evaluate().map(ProbeStatus(this, _))
  final def labeled[L](label: L) = LabeledProbe(label, this)
}

sealed trait ProbeResult {
  val msg: String
}

final case class ProbeFailure(msg: String = "") extends ProbeResult
final case class ProbeSuccess(msg: String = "") extends ProbeResult

object errorHandler {
  def errorToFailure[F[_]](
      implicit F: Effect[F]): PartialFunction[Throwable, F[ProbeResult]] = {
    case e => F.pure(ProbeFailure(e.toString): ProbeResult)
  }
}
