import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._

val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val start = System.currentTimeMillis();

val df = sc.textFile(config.logDirectory.getPath).map(LogEntryUtils.parseLogLine).toDF()

df.filter($"successfulParsing").write.partitionBy("year", "month", "day").format("parquet").save(config.parquetFile.getPath)

val errors = df.filter(!$"successfulParsing")

if(errors.count > 0) {
  println("Errors:" )
  errors.collect.foreach(println)
  println(" ")
  println("Finished with " + errors.count + " errors in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")
}else {
  println("Finished successfully in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")
}