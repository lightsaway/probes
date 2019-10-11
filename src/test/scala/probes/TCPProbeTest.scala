package probes

import cats.effect.IO
import org.scalatest.{FunSuite, Matchers}

class TCPProbeTest extends FunSuite with Matchers {

  test("connection refused") {
    val p = TCPProbe[IO](Location("0.0.0.0", 5000), "", Warning)
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeFailure]
    r.result.msg should include("refused")
    r.probe shouldBe p
  }

}
