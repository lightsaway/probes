package lightsaway.probes

import org.apache.kafka.common.{Metric, MetricName}
import scala.jdk.CollectionConverters._

object utils {
  def getMetric(number: Double, name: String = "connection-count"): Metric =
    new Metric {
      override def metricName(): MetricName =
        new MetricName(name, "", "", Map.empty[String, String].asJava)
      override def value(): Double = 0.0
      override def metricValue(): Double = number
    }
}
