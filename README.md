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

## Choose/modify/create a config file

This config file contains where the log files are kept and where the parquet folder (structured file) should be written 

Example for STRING. 
In this example we use /scratch/local but we could use /scratch/cluster if we wanted to run in the cluster

```
name: STRING

#Directory where to find the log files
logDirectory: /scratch/local/weekly/dteixeir/string-logs/*

#Directory where to output or read the parquet file
parquetFile: /scratch/local/weekly/dteixeir/string-parquet/

```

## Run the application

```shell
./start.sh configs/oma-config.yaml
```

Choose the appropriated option (option 2 and 3, requires option 1 parquet)

```
Using config file configs/oma-config.yaml
---
1) Convert Parquet
2) Insights Report
3) Distinct IPs
4) Quit
`
```

### Parquet (Required)

Option 1, Convert Parequet is required to proceed further.
This converstion will convert the "raw log files" to a structured / indexed format for fast analysis.

### Insights report
This option will generate a report to be included in Insights 

### Distinct IPs
This will produce a file with all distinct IPs

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
