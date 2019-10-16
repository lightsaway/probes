import cats.effect.Effect
import org.http4s.{Request, Status, Uri}
import org.http4s.client.Client
import probes._
import org.http4s.Method._
import cats.syntax.either._
import cats.syntax.applicativeError._

case class HttpGetProbe[F[_]](
    uri: Uri,
    override val name: String,
    override val severity: Severity)(implicit F: Effect[F], client: Client[F])
    extends Probe[F](name, severity) {
  override protected def evaluate(): F[Either[ProbeFailure, ProbeSuccess]] = {
    val r: F[Either[ProbeFailure, ProbeSuccess]] =
      client.fetch(Request[F](GET, uri)) {
        case Status.Successful(r) =>
          F.pure(
            ProbeSuccess(s"Request succeded with status ${r.status.code}").asRight)
        case r =>
          F.pure(
            ProbeFailure(s"Request $r failed with status ${r.status.code}").asLeft)
      }
    r.handleErrorWith(errorHandler.defaultErrorHandler[F])
  }

}
