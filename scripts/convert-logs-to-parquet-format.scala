import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._

val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val start = System.currentTimeMillis();

val df = sc.textFile(config.logDirectory.getPath).map(LogEntryUtils.parseLogLine).toDF()

if(config.firstLevelPathFilter.isDefined){
  df = df.filter($"firstLevelPath" === config.firstLevelPathFilter)
}

df.filter($"successfulParsing").write.partitionBy("year", "month", "day").format("parquet").save(config.parquetFile.getPath)

val errors = df.filter(!$"successfulParsing")
val totalCount = df.count
val errorsCount = errors.count

if(errorsCount > 0) {
  println("Errors:" )
  errors.collect.foreach(println)
  println("Finished with " + errorsCount + " errors in a total of  " + totalCount + " logged lines in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")
  val ratio = errorsCount.toDouble / totalCount
  if(ratio > 0.01){ //Bigger than 1% consider it as a failure!
    throw new RuntimeException("More than 1% threashold! Failed to convert data.")
  }
  
}else {
  println("Finished successfully in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")
}