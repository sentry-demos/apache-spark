package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Dataset
import example.sentryUtils.SentrySpark
import example.sentryUtils.SentryListener

import io.sentry.Sentry

object SimpleApp {
  def main(args: Array[String]) {
    val sparkHome = sys.env("SPARK_HOME")
    val logFile = sparkHome.concat("/README.md")

    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .config("spark.extraListeners", "example.sentryUtils.SentryListener")
      .getOrCreate()

    SentrySpark.init(spark.sparkContext)

    val logData = spark.read.textFile(logFile).cache()

    val numAs = logData.filter(line => {
      val k = 1 / 0
      line.contains("a")
    }).count()
    val numBs = logData.filter(line => line.contains("b")).count()

    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}
