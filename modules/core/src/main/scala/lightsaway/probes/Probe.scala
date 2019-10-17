package lightsaway.probes
import cats.effect.Effect
import cats.implicits._
sealed trait Severity
case object Warning extends Severity
case object Critical extends Severity

case class ProbeStatus[F[_]](probe: Probe[F], result: ProbeResult)

abstract class Probe[F[_]: Effect](
    val name: String,
    val severity: Severity
) {
  def evaluate(): F[ProbeResult]
  final def status(): F[ProbeStatus[F]] = evaluate().map(ProbeStatus(this, _))
}

sealed trait ProbeResult {
  val msg: String
}
final case class ProbeFailure(msg: String = "") extends ProbeResult
final case class ProbeSuccess(msg: String = "") extends ProbeResult

object errorHandler {
  def defaultErrorHandler[F[_]](
      implicit F: Effect[F]): PartialFunction[Throwable, F[ProbeResult]] = {
    case e => F.pure(ProbeFailure(e.toString): ProbeResult)
  }
}
