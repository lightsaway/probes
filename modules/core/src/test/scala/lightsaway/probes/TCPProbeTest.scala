package lightsaway.probes
import cats.effect.IO
import com.github.tomakehurst.wiremock.WireMockServer
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}

class TCPProbeTest extends FunSuite with Matchers with BeforeAndAfterEach{
  private val port = 8080
  private val wireMockServer = new WireMockServer(8080)

  override def beforeEach: Unit = {
    wireMockServer.start()
  }

  override def afterEach: Unit = {
    wireMockServer.stop()
  }

  test("connection refused") {
    val p = TCPProbe[IO](Location("0.0.0.0", 5000), "", Warning)
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeFailure]
    r.result.msg should include("refused")
    r.probe shouldBe p
  }

  test("connection accepted") {
    val p = TCPProbe[IO](Location("0.0.0.0", 8080), "", Warning)
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeSuccess]
    r.probe shouldBe p
  }
}

