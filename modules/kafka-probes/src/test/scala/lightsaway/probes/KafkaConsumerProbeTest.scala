package lightsaway.probes

import cats.effect.IO
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.{Metric, MetricName}
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.ExecutionContext
import utils._

class KafkaConsumerProbeTest
    extends FunSuite
    with Matchers
    with MockitoSugar
    with ArgumentMatchersSugar {
  implicit val ec: ExecutionContext = ExecutionContext.global
  type MetricsMap = java.util.Map[MetricName, Metric]

  class Fixture {
    val metrics = new java.util.HashMap[MetricName, Metric]()
    val kafkaConsumerMock = mock[KafkaConsumer[String, String]]
  }

  test("no metrics returns failure, metric connection-count not available") {
    val metrics = new java.util.HashMap[MetricName, Metric]()
    val kafkaConsumerMock = mock[KafkaConsumer[String, String]]

    val healthCheck = KafkaConsumerHealthCheck[IO](kafkaConsumerMock)()
    when(kafkaConsumerMock.metrics.asInstanceOf[MetricsMap]).thenReturn(metrics)
    val result = healthCheck.status().unsafeRunSync.result
    result shouldBe ProbeFailure(s"metric connection-count not available")
  }

  test("available metric with 0 connections returns failure, couldn't connect") {
    val f = new Fixture
    val probe = KafkaConsumerHealthCheck[IO](f.kafkaConsumerMock)()
    val metric = getMetric(0.0)
    f.metrics.put(metric.metricName(), metric)
    when(f.kafkaConsumerMock.metrics.asInstanceOf[MetricsMap])
      .thenReturn(f.metrics)

    val result = probe.status().unsafeRunSync.result
    result shouldBe ProbeFailure("unable to connect to kafka")
  }

  test("available metric with 1 connections returns success") {
    val f = new Fixture
    val healthCheck = KafkaConsumerHealthCheck[IO](f.kafkaConsumerMock)()
    val metric = getMetric(1.0)
    f.metrics.put(metric.metricName(), metric)
    when(f.kafkaConsumerMock.metrics.asInstanceOf[MetricsMap])
      .thenReturn(f.metrics)
    healthCheck.status().unsafeRunSync.result shouldBe ProbeSuccess(
      "Connection count: 1.0")

  }


}


