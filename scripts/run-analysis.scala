val conf = new SparkConf().setAppName("Server Log Analysis")
val sc = new SparkContext(conf)
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._

val start = System.currentTimeMillis();

val df = sc.textFile(config.getOrElse("FILE_NAME", "sample-mixed-uniprot-logs.log")).map(LogEntryUtils.parseLogLine).toDF()

//df.cache()
df.write.partitionBy("year", "month", "day")
  .format("parquet").save(config.getOrElse("PARQUET_FILE", "apache-logs"))
