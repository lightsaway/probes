package lightsaway.probes

import java.lang.System.currentTimeMillis

import cats.implicits._
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

import cats.effect.{Effect, IO}
case class Location(ip: String, port: Int)

//TODO make fs2.Stream to repeat untill timeout or success
case class TCPProbe[F[_]](location: Location, n: String, s: Severity)(
    implicit F: Effect[F])
    extends Probe[F](n, s) {
  override def evaluate: F[Either[ProbeFailure, ProbeSuccess]] =
    F.bracket(
      F.delay {
        SocketChannel.open(new InetSocketAddress(location.ip, location.port))
      }.attempt
    ) {
      case Left(e) => F.pure(ProbeFailure(e.getMessage).asLeft[ProbeSuccess])
      case Right(r) =>
        if (r.isConnected) {
          F.pure(ProbeSuccess().asRight[ProbeFailure])
        } else {
          F.pure(ProbeFailure().asLeft[ProbeSuccess])
        }
    } {
      case Left(_)  => F.unit
      case Right(r) => F.delay(r.close()).as(())
    }

}
