package lightsaway.probes

import cats.effect.{Blocker, IO}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import org.scalatest.{FunSuite, Matchers}

class PostgresConnectionProbeTest extends FunSuite with Matchers {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  implicit val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql:world",       // connect URL (driver-specific)
    "postgres",                   // user
    "",                          // password
  )

  implicit val blocker = Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing

  test("no connection"){
    val probe = PostgresConnectionProbe[IO]().status().unsafeRunSync()
    probe.result shouldBe a[ProbeFailure]
    probe.result.msg should include("Unable to connect to the db ")
  }
}
