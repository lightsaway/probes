package lightsaway.probes

import lightsaway.probes.errorHandler.errorToFailure
import cats.implicits._
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

import cats.effect.{Effect, IO}
case class Location(ip: String, port: Int)

//TODO make fs2.Stream to repeat untill timeout or success
case class TCPProbe[F[_]](location: Location)(implicit F: Effect[F])
    extends Probe[F] {
  override def evaluate: F[ProbeResult] =
    F.bracket(
        F.delay {
          SocketChannel.open(new InetSocketAddress(location.ip, location.port))
        }
      ) { socket =>
        if (socket.isConnected) {
          F.pure(ProbeSuccess(): ProbeResult)
        } else {
          F.pure(ProbeFailure(): ProbeResult)
        }
      } { socket =>
        F.delay(socket.close()).as(())
      }
      .handleErrorWith(errorToFailure[F])

}
