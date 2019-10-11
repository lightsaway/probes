package probes

import java.net.ServerSocket

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

  test("connection accepted") {
    val port = freePort_?()
    new Thread(new Server(port)).start()
    val p = TCPProbe[IO](Location("0.0.0.0", port), "", Warning)
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeSuccess]
    r.probe shouldBe p
  }

  def freePort_?(): Int = {
    val server = new ServerSocket(0)
    val port = server.getLocalPort
    server.close()
    port
  }
}

class Server(port: Int) extends Runnable {
  def run: Unit = {
    val server = new ServerSocket(port)
    server.accept()
  }
}
