import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import io.sentry.spark.SentrySpark;

import io.sentry.Sentry;

object SimpleApp {
  def main(args: Array[String]) {
    val sparkHome = sys.env("SPARK_HOME")
    val logFile = sparkHome.concat("/README.md")

    Sentry.init()

    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .config("spark.extraListeners", "io.sentry.spark.listener.SentrySparkListener")
      .getOrCreate()

    SentrySpark.applyContext(spark.sparkContext)

    val logData = spark.read.textFile(logFile).cache()

    val numAs = logData.filter(line => {
      throw new IllegalStateException("Exception thrown");
      line.contains("a")
    }).count()
    val numBs = logData.filter(line => line.contains("b")).count()

    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}