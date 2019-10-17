package lightsaway.probes

import java.nio.file.{Files, Path}

import cats.effect.Effect
import cats.syntax.either._

case class LocalFileExists[F[_]](
    path: Path,
    override val name: String,
    override val severity: Severity)(implicit F: Effect[F])
    extends Probe[F](name, severity) {
  override def evaluate: F[ProbeResult] =
    if (Files.exists(path) && !Files.isDirectory(path)) {
      F.pure(ProbeSuccess(s"file exists at ${path}"))
    } else {
      F.pure(ProbeFailure(s"file is missing at ${path}"))
    }
}
