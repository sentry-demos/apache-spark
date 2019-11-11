/* SimpleApp.scala */
import org.apache.spark.sql.SparkSession

import io.sentry.Sentry

import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level

object SimpleApp {
  @transient lazy val logger = Logger.getLogger(SimpleApp.getName)

  def main(args: Array[String]) {
    logger.error("This is an error")

    try
      1 / 0
    catch {
      case e: Exception =>
        Sentry.capture(e)
    }

    val sparkHome = sys.env("SPARK_HOME")
    val logFile = sparkHome.concat("/README.md")

    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()

    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()

    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}
