import org.apache.spark.sql.{SparkSession, Dataset}
import org.apache.spark.sql.execution.streaming.MemoryStream
import io.sentry.spark.SentrySpark;

import io.sentry.Sentry;

object SimpleStreamingQueryApp {
  def main(args: Array[String]) {
    Sentry.init()

    val spark = SparkSession
      .builder
      .appName("Simple Application")
      .config("spark.sql.streaming.streamingQueryListeners", "io.sentry.spark.listener.SentryStreamingQueryListener")
      .getOrCreate()

    SentrySpark.applyContext(spark.sparkContext)

    import spark.implicits._
    implicit val sqlContext = spark.sqlContext

    val input = List(List(1), List(2, 3))
    def compute(input: Dataset[Int]): Dataset[Int] = {
      input.map(elem => {
        val k = 3 / 0
        elem + 3
      })
    }

    val inputStream = MemoryStream[Int]
    val transformed = compute(inputStream.toDS())

    val queryName = "Query Name"

    val query =
      transformed.writeStream.format("memory").outputMode("append").queryName(queryName).start()

    input.foreach(batch => inputStream.addData(batch))

    query.processAllAvailable()
    val table = spark.table(queryName).as[Int]
    val resultRows = table.collect()

    spark.stop()
  }
}