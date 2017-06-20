# Scala Library used to analyse Apache Server Logs using Spark 

[![Build Status](https://travis-ci.org/sib-swiss/server-log-analytics.svg?branch=master)](https://travis-ci.org/sib-swiss/server-log-analytics)

**PROJECT UNDER ACTIVE DEVELOPMENT**

## Build the artifact
Requirements: Download Spark 2.1+ and set $SPARK_HOME environment variable

```shell
sbt package
```

## Set the environment

```shell
SPARK_HOME=$HOME/spark
CMD="$SPARK_HOME/bin/spark-shell --executor-memory 1g --driver-memory 1g --jars lib/moultingyaml_2.11-0.4.0.jar,lib/snakeyaml-1.18.jar,target/scala-2.11/server-log-analytics_2.11-1.0.jar"
DEFAULT_CONFIG="--conf spark.driver.extraJavaOptions=\"-Dconfig.file=default-config.yaml\""
```

## First step: Convert the apache logs to parquet format
```shell
$CMD -i scripts/convert-logs-to-parquet-format.scala $DEFAULT_CONFIG
```


## Second step: Run the analysis on the parquet formatted logs
```shell
spark-shell --driver-memory 60g -i analyse.scala
```

## Optional: Run the analysis on a cluster
```shell
$SPARK_HOME/bin/spark-submit --class org.elixir.insights.server.logs.ServerLogAnalyser --master local[4] target/scala-2.11/server-log-analytics_2.11-1.0.jar
```

32 cores
100GB of memory

./start-slave.sh -c 32 -m 100G spark://rserv01.vital-it.ch:7077


Took 2.5 hours
```
spark-shell --executor-memory 100G --master spark://rserv01.vital-it.ch:7077 -i analyse.scala
```
