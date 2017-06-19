package swiss.sib.analytics.server.logs

import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext._
import org.apache.spark.sql.functions._
import swiss.sib.analytics.server.logs.custom.uniprot.UniProtService
import swiss.sib.analytics.server.logs.model.LogEntry
import swiss.sib.analytics.server.logs.services.LocationService
import swiss.sib.analytics.server.logs.utils.LogEntryUtils
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.Column

case class ApacheAccessLog(hostname: String, ipAddress: String, clientIdentd: String, userId: String, dateTime: String, method: String, endpoint: String, protocol: String, responseCode: Int, contentSize: String, something: String, agent: String, time: String, charset: String, ip2: String, number: String) {}

object ServerLogAnalyser {

  val fw = new java.io.FileWriter("results.tsv")
  val GB_FACTOR = scala.math.pow(1024.0, 3);
  val config = scala.io.Source.fromFile("config.properties").getLines().filter(l => (!l.startsWith("#") && !l.trim().isEmpty())).map(l => { val v = l.split("="); (v(0), v(1)) }).toMap

  def prt(s: String) = {
    println(s); fw.write(s + "\n"); fw.flush;
  }

  def main(args: Array[String]) {

    //Logger.getLogger("org").setLevel(Level.OFF);
    //Logger.getLogger("akka").setLevel(Level.OFF);

    val conf = new SparkConf().setAppName("Server Log Analysis")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._

    val start = System.currentTimeMillis();

    val df = sc.textFile(config.getOrElse("FILE_NAME", "sample-mixed-uniprot-logs.log")).map(LogEntryUtils.parseLogLine).toDF()

    //df.cache()
    df.write.partitionBy("year", "month", "day")
      .format("parquet").save(config.getOrElse("PARQUET_FILE", "apache-logs"))

    /*
    val maxResults = 20;
    val dimensions = List("hostname",
      "responseInfo.contentPresent", "responseInfo.charset",
      "agentInfo.isBot", "agentInfo.isProgram",
      "agentInfo.bot", "agentInfo.program",
      "requestInfo.url", "requestInfo.firstLevelPath",
      "locationInfo.city", "locationInfo.country",
      "entryType.database", "entryType.accession", "clientInfo.ipAddress");

    List(("hits", count("*") as "hits"),
      ("throughput", round(sum("responseInfo.contentSize") / GB_FACTOR, 6) as "throughput"),
      ("distinct-ips", countDistinct("clientInfo.ipAddress") as "distinct-ips"))
      .foreach(m => {
        m match {
          case (metric, function) => {

            prt(metric + "_per_months_ ################################# ")

            //Shows metrics by date
            df.groupBy($"date.month" as "month", $"date.year" as "year")
              .agg(function)
              .orderBy($"month" asc, $"year" asc).collect().foreach(r => prt(r.mkString("\t")));

            //Shows metrics by dimensions
            dimensions.foreach(dimension => {

              prt("\n## Top_" + maxResults + "_" + metric + "_for_" + dimension + " ####")

              val data = df.groupBy($"""$dimension""")
                .agg(function)
                .orderBy($"""$metric""" desc).limit(maxResults).foreach(r => prt(r.mkString("\t")))

            })

            //Special case to show top entries by accession type
            List("trembl", "swissprot").foreach(db => {
              prt("\nTop_" + maxResults + "_" + metric + "_for_proteins_in_entryType.database_=_" + db + " ############");
              val data = df
                .filter($"entryType.database" === db)
                .groupBy($"entryType.accession")
                .agg(function)
                .orderBy($"""$metric""" desc).limit(maxResults).foreach(r => prt(r.mkString("\t")))
            })

            prt("\n");

          }
        }
      })
    */
    prt("Finished in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")

    fw.close()
    sc.stop

  }

}
