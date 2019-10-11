package probes
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
  protected def evaluate(): F[Either[ProbeFailure, ProbeSuccess]]

  final def status(): F[ProbeStatus[F]] = evaluate().map {
    case Left(r)  => ProbeStatus(this, r)
    case Right(r) => ProbeStatus(this, r)
  }
}

sealed trait ProbeResult {
  val msg: String
}
final case class ProbeFailure(msg: String = "") extends ProbeResult
final case class ProbeSuccess(msg: String = "") extends ProbeResult
