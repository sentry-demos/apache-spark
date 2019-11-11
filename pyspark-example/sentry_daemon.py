import os

from dotenv import load_dotenv

import pyspark.daemon as original_daemon

import sentry_sdk
from sentry_sdk.integrations.spark import SparkWorkerIntegration

if __name__ == '__main__':
    load_dotenv()
    SENTRY_DSN = os.getenv("SENTRY_DSN")

    sentry_sdk.init(dsn=SENTRY_DSN, integrations=[SparkWorkerIntegration()], environment="local_worker")
    original_daemon.manager()
