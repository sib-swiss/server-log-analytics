import swiss.sib.analytics.server.logs._
import org.apache.spark.sql.DataFrame
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val df = sqlContext.read.parquet(config.parquetFile.getPath)


val fileName = readLine("Save Distinct IP file")

//Logger.getLogger("org").setLevel(Level.OFF);
//Logger.getLogger("akka").setLevel(Level.OFF);
 
//This lists of all IP
val resultDF = df.groupBy($"""clientInfo.ipAddress""").agg(count("*") as "hits").orderBy($"hits" desc)

resultDF.coalesce(1).write.option("header", "true").csv(fileName)
