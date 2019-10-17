package lightsaway.probes

import cats.effect.IO
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.{Metric, MetricName, Node, PartitionInfo}
import org.mockito.MockitoSugar
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._
import utils._

class KafkaProducerProbeTest extends FunSuite with Matchers with MockitoSugar {
  implicit val ec: ExecutionContext = ExecutionContext.global
  type MetricsMap = java.util.Map[MetricName, Metric]

  final val ERROR_RATE = "record-error-rate"

  class Fixture {
    val metrics: MetricsMap = new java.util.HashMap[MetricName, Metric]()
    val kafkaProducerMock = mock[KafkaProducer[String, String]]
  }

  test(
    "no metrics returns probeFailure, metric record-error-rate not available") {
    val f = new Fixture
    implicit val k = f.kafkaProducerMock
    val probe = ProducerErrorRateProbe[IO]()

    when(f.kafkaProducerMock.metrics.asInstanceOf[MetricsMap])
      .thenReturn(f.metrics)

    val result = probe.status().unsafeRunSync().result
    result shouldBe ProbeFailure(s"metric ${ERROR_RATE} not available")
  }

  test(
    "wrong metrics returns probeFailure, metric record-error-rate not available") {
    val f = new Fixture
    implicit val k = f.kafkaProducerMock
    val probe = ProducerErrorRateProbe[IO]()
    when(f.kafkaProducerMock.metrics.asInstanceOf[MetricsMap])
      .thenReturn(f.metrics)

    val result = probe.status().unsafeRunSync().result
    result shouldBe ProbeFailure(s"metric ${ERROR_RATE} not available")
  }

  test("no partitions returns probeFailure") {
    val f = new Fixture
    implicit val k = f.kafkaProducerMock
    val probe = KafkaProducerTopicProbe[IO]("topic")

    when(f.kafkaProducerMock.partitionsFor("topic"))
      .thenReturn(List[PartitionInfo]().asJava)

    val result = probe.status().unsafeRunSync().result
    result shouldBe ProbeFailure(s"Couldn't find any partition for topic topic")
  }

  test("one partitions returns healthy") {
    val f = new Fixture
    implicit val k = f.kafkaProducerMock
    val probe = KafkaProducerTopicProbe[IO]("topic")
    val node = new Node(0, "", 0)

    when(f.kafkaProducerMock.partitionsFor("topic"))
      .thenReturn(
        List(
          new PartitionInfo("",
                            1,
                            node,
                            List(node).toArray,
                            List(node).toArray)).asJava)

    val result = probe.status().unsafeRunSync().result
    result shouldBe ProbeSuccess("found 1 partitions")
  }

  test("available metric with error rate > 0 returns probeFailure, ") {
    val f = new Fixture
    implicit val k = f.kafkaProducerMock
    val probe = ProducerErrorRateProbe[IO]()
    val metric = getMetric(1.0, ERROR_RATE)
    f.metrics.put(metric.metricName(), metric)

    when(f.kafkaProducerMock.metrics.asInstanceOf[MetricsMap])
      .thenReturn(f.metrics)

    val result = probe.status().unsafeRunSync().result
    result shouldBe a[ProbeFailure]

  }

  test("available metric with error rate == 0 returns empty Right") {
    val f = new Fixture
    implicit val k = f.kafkaProducerMock
    val probe = ProducerErrorRateProbe[IO]()
    val metric = getMetric(0.0, ERROR_RATE)
    f.metrics.put(metric.metricName(), metric)

    when(f.kafkaProducerMock.metrics.asInstanceOf[MetricsMap])
      .thenReturn(f.metrics)

    val result = probe.status().unsafeRunSync().result
    result shouldBe a[ProbeSuccess]
  }
}
