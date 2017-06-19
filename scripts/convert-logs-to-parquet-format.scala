import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._

val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val start = System.currentTimeMillis();

val df = sc.textFile(config.logDirectory.getPath).map(LogEntryUtils.parseLogLine).toDF()

df.write.partitionBy("year", "month", "day").format("parquet").save(config.parquetFile)

println("Finished in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")
