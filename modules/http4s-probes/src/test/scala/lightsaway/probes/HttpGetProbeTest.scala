package lightsaway.probes

import java.util.concurrent._

import cats.effect.{Blocker, IO}
import lightsaway.probes
import lightsaway.probes.{HttpGetProbe, ProbeFailure, ProbeSuccess, Warning}
import org.http4s.client.JavaNetClientBuilder
import org.http4s.implicits._
import org.scalatest.{FunSuite, Matchers}

class HttpGetProbeTest extends FunSuite with Matchers {
  implicit val contextShift =
    IO.contextShift(scala.concurrent.ExecutionContext.global)

  val blockingPool = Executors.newFixedThreadPool(5)
  val blocker = Blocker.liftExecutorService(blockingPool)
  implicit val client = JavaNetClientBuilder[IO](blocker).create

  test("http probe is positive") {
    //fixme : start mock server
    val p = HttpGetProbe[IO](uri"https://www.yahoo.com/")
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeSuccess]
    r.result.msg should include("status 200")
    r.probe shouldBe p
  }

  test("http probe is negative") {
    val p =
      probes
        .HttpGetProbe[IO](uri"http://somerandomwebsite.eu")
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeFailure]
    r.result.msg.toLowerCase should include("unknownhost")
    r.probe shouldBe p
  }
}
