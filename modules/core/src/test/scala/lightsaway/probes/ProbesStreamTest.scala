package lightsaway.probes

import java.io.File

import cats.effect.IO
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
class ProbesStreamTest extends FunSuite with Matchers {
  implicit val contextShift =
    IO.contextShift(scala.concurrent.ExecutionContext.global)

  test("creates stream") {

    val probes =
      List.fill(3)(LocalFileExists[IO](new File("/not/a/path").toPath))
    val res = ProbesStream[IO](probes).compile.toList.unsafeRunSync().flatten
    res.size shouldBe 3
    every(res.map(_.result)) shouldBe a[ProbeFailure]
  }

  test("runs through pipe") {
    val probes = (1 to 3).toList.map(i =>
      LocalFileExists[IO](new File("/not/a/path").toPath))
    val store = ProbeStore[IO]().unsafeRunSync()
    val res = ProbesStream[IO](probes)
      .evalTap(ProbesSink[IO](store))
      .compile
      .toList
      .unsafeRunSync()
      .flatten
    res.size shouldBe 3
    every(res.map(_.result)) shouldBe a[ProbeFailure]

    val updated = store.get.unsafeRunSync().values.toList
    updated should contain allElementsOf (res)
  }
}
