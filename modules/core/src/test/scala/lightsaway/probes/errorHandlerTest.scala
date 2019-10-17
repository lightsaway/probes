package lightsaway.probes

import cats.effect.IO
import org.scalatest.{FunSuite, Matchers}

class errorHandlerTest extends FunSuite with Matchers {

  test("testDefaultErrorHandler") {
    val e = IO
      .raiseError(new Throwable("baad day"))
      .handleErrorWith(errorHandler.defaultErrorHandler[IO])
      .unsafeRunSync()
    e shouldBe a[ProbeFailure]
    e.msg shouldBe "java.lang.Throwable: baad day"
  }

}
