import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types._;

import io.sentry.Sentry
import io.sentry.spark.SentrySpark;

object SimpleQueryApp {
  def main(args: Array[String]): Unit = {
    Sentry.init()

    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.sql.queryExecutionListeners", "io.sentry.spark.listener.SentryQueryExecutionListener")
      .getOrCreate()

    SentrySpark.applyContext(spark.sparkContext)

    import spark.implicits._

    runBasicDataFrameExample(spark)

    spark.stop()
  }

  private def runBasicDataFrameExample(spark: SparkSession): Unit = {
    val people = spark.sparkContext.makeRDD("""{"name":"Michael"}{"name":"Andy", "age":30}{"name":"Justin", "age":19}""" :: Nil)
    val df = spark.read.json(people)

    df.show()

    import spark.implicits._

    df.printSchema()

    df.select("name").show()

    df.createOrReplaceTempView("people")

    val sqlDF = spark.sql("SELECT * FROM people")

    sqlDF.show()
  
    df.createGlobalTempView("people")

    spark.sql("SELECT * FROM global_temp.people").show()

    spark.newSession().sql("SELECT * FROM global_temp.people").show()

    df.select($"name", $"age" + 1).show()
  }
}