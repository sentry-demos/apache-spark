# pyspark-example

This demo covers using Apache Beam with the Python SDK with a simple application.

It leverages the `SparkIntegration` and the `SparkWorkerIntegration`.

This demo uses `pyspark` 2.4.4 and `sentry-sdk` 0.13.2

## First Time Setup

Download Apache Spark version 2.4.4 with Hadoop 2.7 - https://spark.apache.org/downloads.html

Set your `$SPARK_HOME` environmental variable to point to your Spark folder.

```
export SPARK_HOME=path/to/spark/spark-2.4.4-bin-hadoop2.7
```

The Beam SDK requires Python 2 users to use Python 2.7 and Python 3 users to use Python 3.4 or higher. For this demo, we will be using Python 3.

Check your your python3 version and make sure it is 3.5 or above.

```bash
python3 --version
```

Setup and activate a Python3 environment.

```bash
python3 -m pip install virtualenv
python3 -m virtualenv .venv
source .venv/bin/activate
```

Install all required dependencies.

```
pip install -r requirements.txt
```

Add your Sentry DSN to the `.env` file OR add it to your environmental variables.

> .env
```
SENTRY_DSN=__PUBLIC_DSN_HERE__
```

If needed, you can deactivate your virtualenv using:

```bash
deactivate
```

## Run

Make sure you have your virtualenv running.

```bash
source .venv/bin/activate
```

Run the example application.

```bash
$SPARK_HOME/bin/spark-submit \
  --master "local[4]" \
  --py-files "sentry_daemon.py" \
  --conf "spark.python.use.daemon=true" \
  --conf "spark.python.daemon.module=sentry_daemon" \
  simple_app.py
```

It should fail and an error should show up in your Sentry project.

## Troubleshooting

### Pip issues?

Make sure pip version `7.0.0` or above is installed

```bash
pip --version
```

You can upgrade pip using

```bash
pip install --upgrade pip
```

### Sentry not installed on your cluster?

If you are running this example on an execution environment that is not your local machine, you will have to install the `sentry-sdk` on each of your executors.

You can either generate a `.zip` or `.egg` file and pass it to spark using the `--py-files` option or use a bootstrap script to `pip install sentry-sdk` on your clusters.
