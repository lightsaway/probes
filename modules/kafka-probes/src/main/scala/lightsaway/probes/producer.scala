package lightsaway.probes

import cats.effect.Effect
import cats.syntax.either._
import org.apache.kafka.clients.producer.KafkaProducer

import scala.jdk.CollectionConverters._

case class KafkaProducerTopicProbe[F[_]](topic: String)(
    implicit F: Effect[F],
    producer: KafkaProducer[_, _])
    extends Probe[F] {
  override def evaluate(): F[ProbeResult] =
    F.delay {
      val p = producer.partitionsFor(topic).asScala
      if (p.nonEmpty) ProbeSuccess(s"found ${p.size} partitions")
      else
        ProbeFailure(
          s"Couldn't find any partition for topic ${topic}"
        )
    }
}

case class ProducerErrorRateProbe[F[_]]()(implicit F: Effect[F],
                                          producer: KafkaProducer[_, _])
    extends Probe[F] {
  private final val ERROR_RATE = "record-error-rate"
  override def evaluate(): F[ProbeResult] = F.delay {
    (for {
      errorRate <- producer
        .metrics()
        .asScala
        .values
        .find(_.metricName().name() == ERROR_RATE)
      // metricValue returns Object - yaak
      rate = errorRate.metricValue().asInstanceOf[Double]
      status = if (rate == 0.0) ProbeSuccess("error rate is 0")
      else ProbeFailure(s"kafka error rate($errorRate) > 0")
    } yield status).getOrElse(ProbeFailure(s"metric $ERROR_RATE not available"))

  }
}
