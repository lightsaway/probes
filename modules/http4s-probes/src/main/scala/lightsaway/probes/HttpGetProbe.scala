package lightsaway.probes

import cats.effect.Effect
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.{Request, Status, Uri}
import cats.syntax.applicativeError._
import cats.syntax.either._

case class HttpGetProbe[F[_]](uri: Uri)(implicit F: Effect[F],
                                        client: Client[F])
    extends Probe[F] {
  override def evaluate(): F[ProbeResult] = {
    val r: F[ProbeResult] =
      client.fetch(Request[F](GET, uri)) {
        case Status.Successful(r) =>
          F.pure(ProbeSuccess(s"Request succeded with status ${r.status.code}"))
        case r =>
          F.pure(
            ProbeFailure(s"Request $r failed with status ${r.status.code}"))
      }
    r.handleErrorWith(errorHandler.errorToFailure[F])
  }

}
