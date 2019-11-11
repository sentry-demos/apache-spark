#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from __future__ import print_function

import sys
import os
from random import random

from dotenv import load_dotenv

from pyspark.sql import SparkSession

import sentry_sdk
from sentry_sdk.integrations.spark import SparkIntegration

if __name__ == "__main__":
    """
        Usage: pi [partitions]
    """
    load_dotenv()
    SENTRY_DSN = os.getenv("SENTRY_DSN")

    sentry_sdk.init(dsn=SENTRY_DSN, integrations=[SparkIntegration()], environment="local_driver")

    spark = SparkSession\
        .builder\
        .appName("PythonPi")\
        .getOrCreate()

    with sentry_sdk.configure_scope() as scope:
        scope.set_tag("a_driver_tag", "This should be in driver")

    partitions = int(sys.argv[1]) if len(sys.argv) > 1 else 2
    n = 100000 * partitions

    def f(_):
        x = random() * 2 - 1
        y = random() * 2 - 1
        return 1 if x ** 2 + y ** 2 <= 1 else 0

    def bad_add(a, b):
        # Throw error here
        with sentry_sdk.configure_scope() as scope:
            scope.set_tag("worker_specific_tag", "This should be in worker")
        l = 3 / 0
        return a + b

    count = spark.sparkContext.parallelize(range(1, n + 1), partitions).map(f).reduce(bad_add)
    print("Pi is roughly %f" % (4.0 * count / n))

    spark.stop()
