import cats.effect.{Blocker, ContextShift, Effect}
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import lightsaway.probes.{Probe, ProbeFailure, ProbeSuccess, Severity}
import doobie.implicits._
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.either._

class DatabaseHealthCheck[F[_]: Effect](name: String, severity: Severity)(
  implicit blocker: Blocker, tx: Transactor[F], cs: ContextShift[F]
) extends Probe[F](
  name, severity
) {
  private val select: ConnectionIO[Int] = sql"SELECT 1".query[Int].unique

  override def evaluate(): F[Either[ProbeFailure, ProbeSuccess]] = blocker.blockOn(
    select.transact(tx).attempt.map {
      case Right(_) => ProbeSuccess("DB Connection established").asRight
      case Left(e) => ProbeFailure(s"Unable to connect to the db : ${e.getMessage}").asLeft
    })
}