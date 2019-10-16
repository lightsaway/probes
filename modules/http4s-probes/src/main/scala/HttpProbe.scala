import cats.effect.Effect
import probes._

case class HttpProbe[F[_]](
    override val name: String,
    override val severity: Severity)(implicit F: Effect[F])
    extends Probe[F](name, severity) {
  override protected def evaluate(): F[Either[ProbeFailure, ProbeSuccess]] = ???
}
