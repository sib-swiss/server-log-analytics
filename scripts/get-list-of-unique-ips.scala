import swiss.sib.analytics.server.logs._
import org.apache.spark.sql.DataFrame
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val start = System.currentTimeMillis();

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val start = System.currentTimeMillis();

val df = sqlContext.read.parquet(config.parquetFile.getPath)

val fileName = readLine("Save Distinct IP file: ")
val year = scala.io.StdIn.readLine("Year (ex. 2016) : ").toInt
 
//This lists of all IP
val resultDF = df.filter($"year" === year).groupBy($"""clientInfo.ipAddress""").agg(count("*") as "hits").orderBy($"hits" desc)

resultDF.coalesce(1).write.option("header", "true").csv(fileName)

println("Finished in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")
