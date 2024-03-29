package lightsaway.probes

import cats.effect.Effect
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.jdk.CollectionConverters._

case class KafkaConsumerHealthCheck[F[_]]()(implicit F: Effect[F],
                                            consumer: KafkaConsumer[_, _])
    extends Probe[F] {
  private val CONNECTION_COUNT = "connection-count"

  override def evaluate(): F[ProbeResult] = F.delay {
    (for {
      connectionCount <- consumer
        .metrics()
        .asScala
        .values
        .find(_.metricName().name() == CONNECTION_COUNT)
      // metricValue returns Object - yaak
      counter = connectionCount.metricValue().asInstanceOf[Double]
      status = if (counter > 0.0) {
        ProbeSuccess(s"Connection count: ${counter}")
      } else {
        ProbeFailure("unable to connect to kafka")
      }
    } yield
      status).getOrElse(ProbeFailure(s"metric $CONNECTION_COUNT not available"))
  }
}
