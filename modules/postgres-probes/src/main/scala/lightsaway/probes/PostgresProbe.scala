package lightsaway.probes

import cats.effect.{Blocker, ContextShift, Effect}
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.either._

case class PostgresConnectionProbe[F[_]: Effect](override val name: String, override val severity: Severity)(
  implicit blocker: Blocker, tx: Transactor[F], cs: ContextShift[F]
) extends Probe[F](
  name, severity
) {
  private val select: ConnectionIO[Int] = sql"SELECT 1".query[Int].unique

  override def evaluate(): F[ProbeResult] = blocker.blockOn(
    select.transact(tx).attempt.map {
      case Right(_) => ProbeSuccess("DB Connection established")
      case Left(e) => ProbeFailure(s"Unable to connect to the db : ${e.getMessage}")
    })
}