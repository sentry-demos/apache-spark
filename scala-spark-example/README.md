# sentry-demos/scala-spark

This demo covers using [Apache Spark](https://spark.apache.org/) with the Scala SDK with a simple application.

This demo uses Apache Spark 2.4.4.

## First Time Setup

Spark requires Java 8. It is recommended that you use [jenv](https://www.jenv.be/) to manage your Java versions.

Check your Java version with:

```bash
java -version
```

You should get something like this:

> openjdk version "1.8.0_222"

Install [sbt](https://www.scala-sbt.org/index.html) with homebrew

```bash
brew install sbt
```

Download Apache Spark - https://spark.apache.org/downloads.html

Set your `$SPARK_HOME` environmental variable to point to your Spark folder.

```
export SPARK_HOME=path/to/spark/spark-2.4.4-bin-hadoop2.7
```

## Run

Package your application jar

```bash
sbt package
```

Run your application with `spark-submit`

```bash
$SPARK_HOME/bin/spark-submit \
  --class "SimpleApp" \
  --master "local[4]" \
  --files "sentry.properties" \
  --packages "io.sentry:sentry-log4j:1.7.27" \
  target/scala-2.11/simple-project_2.11-1.0.jar
```
