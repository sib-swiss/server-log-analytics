name := "Server Log Analytics"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.1"

libraryDependencies += "com.databricks" % "spark-csv_2.11" % "1.5.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "net.jcazevedo" %% "moultingyaml" % "0.4.0"

libraryDependencies += "io.thekraken" % "grok" % "0.1.5"
