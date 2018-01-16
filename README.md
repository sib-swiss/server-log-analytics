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

## 0) Choose or create  a configuration file

## 1) Convert the apache logs to parquet format

### Run the start script

```shell
./start.sh configs/oma-config.yaml
```

Choose the appropriated option

```
Using config file configs/oma-config.yaml
---
1) Convert Parquet
2) Insights Report
3) Distinct IPs
4) Quit
`
```


Option 2: generates report to be integrated in insights
Option 3: script unique IPs - genereate  


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
