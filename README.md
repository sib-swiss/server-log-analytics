# Scala/Spark Library for Server Logs Analytics 

[![Build Status](https://travis-ci.org/sib-swiss/server-log-analytics.svg?branch=master)](https://travis-ci.org/sib-swiss/server-log-analytics)
[![codecov](https://codecov.io/gh/sib-swiss/server-log-analytics/branch/master/graph/badge.svg)](https://codecov.io/gh/sib-swiss/server-log-analytics)

**BE AWARE, THIS PROJECT IS UNDER ACTIVE DEVELOPMENT**

Requirements: 
* [Download Spark 2.1+](https://spark.apache.org/downloads.html)
* Java [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [sbt](http://www.scala-sbt.org/download.html) (to build the project)

## Build the artifact

```shell
sbt package
```

## 1) Convert the apache logs to parquet format

### Set the environment

```shell

#Set Java 8, the settings below are for Vital-IT machines
module add Development/java_jdk/1.8.0_112;
JAVA_HOME=/software/Development/java_jdk/1.8.0_112/

#Set SPARK_HOME environment variable to the folder where SPARK was uncompressed
SPARK_HOME=$HOME/spark #On home directory

CMD="$SPARK_HOME/bin/spark-shell --executor-memory 50g --driver-memory 50g --jars lib/moultingyaml_2.11-0.4.0.jar,lib/snakeyaml-1.18.jar,target/scala-2.11/server-log-analytics_2.11-1.0.jar"
DEFAULT_CONFIG="--conf spark.driver.extraJavaOptions=\"-Dconfig.file=default-config.yaml\""
```

Change the default-config.yml to point to your file.

```shell
$CMD -i scripts/convert-logs-to-parquet-format.scala $DEFAULT_CONFIG
```


## 2) Run the script on the parquet formatted logs

Set the appropriate memory depending on the amount of files to analyse and your machine specs (min: 1g  max: 500g or more...)
```
SPARK_SCRIPT_MEMORY=--driver-memory 10g
```

Choose the appropriated script

### script unique IPs - genereate  

```shell
spark-shell $SPARK_SCRIPT_MEMORY -i scripts/get-list-of-unique-ips.scala
```

### script insights - genereate report to be integrated into [SIB Insights](https://insights.expasy.org/) 
```shell
spark-shell $SPARK_SCRIPT_MEMORY -i scripts/generate-insights-report.scala
```

### script insights - genereate  
```shell
spark-shell $SPARK_SCRIPT_MEMORY -i scripts/analyse.scala
```

## DRAFT - Optional: Run the analysis on a cluster

(The documentation below is not ready)
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
