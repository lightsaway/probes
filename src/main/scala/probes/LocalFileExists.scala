package probes

import java.nio.file.{Files, Path}

import cats.effect.Effect
import cats.syntax.either._

class LocalFileExists[F[_]: Effect](path: Path, n: String, s: Severity)(implicit F: Effect[F]) extends Probe[F](n,s) {
  override def evaluate: F[Either[ProbeFailure, ProbeSuccess]] = if (Files.exists(path)){
    F.pure(ProbeSuccess(s"file exists at ${path}").asRight)
  }
  else{
    F.pure(ProbeFailure(s"file is missing at ${path}").asLeft)
  }
}