import cats.effect.Effect
import org.apache.kafka.clients.producer.KafkaProducer
import probes.{Probe, ProbeFailure, ProbeSuccess, Severity, Warning}
import cats.syntax.either._
import scala.jdk.CollectionConverters._

class KafkaProducerTopicProbe[F[_]](topic: String,
                                    producer: KafkaProducer[_, _])(
    name: String = s"kafka-producer-topic",
    severity: Severity = Warning)(implicit F: Effect[F])
    extends Probe[F](
      name,
      severity
    ) {

  override def evaluate(): F[Either[ProbeFailure, ProbeSuccess]] =
    F.delay({

      val p = producer.partitionsFor(topic).asScala
      if (p.nonEmpty) ProbeSuccess(s"found ${p.size} partitions").asRight
      else
        ProbeFailure(
          s"Couldn't find any partition for topic ${topic}"
        ).asLeft
    })
}

class ProducerErrorRateProbe[F[_]](topic: String, producer: KafkaProducer[_, _])(
    name: String = s"kafka-producer",
    severity: Severity = Warning)(implicit F: Effect[F])
    extends Probe[F](
      name,
      severity
    ) {
  private final val ERROR_RATE = "record-error-rate"
  override def evaluate(): F[Either[ProbeFailure, ProbeSuccess]] = F.delay {
    for {
      errorRate <- producer
        .metrics()
        .asScala
        .values
        .find(_.metricName().name() == ERROR_RATE)
        .toRight(ProbeFailure(s"metric $ERROR_RATE not available"))
      // metricValue returns Object - yaak
      rate = errorRate.metricValue().asInstanceOf[Double]
      status <- rate == 0.0 match {
        case true => ProbeSuccess("error rate is 0").asRight
        case false =>
          ProbeFailure(s"kafka error rate($errorRate) > 0").asLeft
      }
    } yield status

  }
}
