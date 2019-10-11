package probes

import java.io.File
import java.nio.file.Paths

import cats.effect.IO
import org.scalatest.{EitherValues, FunSuite, Matchers}

class LocalFileExistsTest extends FunSuite with Matchers with EitherValues {

  test("no file exists") {
    val p =
      LocalFileExists[IO](new File("/not/a/path").toPath, "filecheck", Warning)
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeFailure]
    r.probe shouldBe p
  }

  test("file exists") {
    val p =
      LocalFileExists[IO](Paths.get(".").toAbsolutePath, "filecheck", Warning)
    val r = p.status().unsafeRunSync()
    r.result shouldBe a[ProbeSuccess]
    r.probe shouldBe p
  }

}
