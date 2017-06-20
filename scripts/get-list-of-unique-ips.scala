import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val df = sqlContext.read.parquet(config.parquetFile.getPath)

//This lists of all IPs
df.groupBy($"""clientInfo.ipAddress""").agg(count("*") as "hits").orderBy($"hits" desc).collect().foreach(println)