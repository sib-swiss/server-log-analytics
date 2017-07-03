import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val df = sqlContext.read.parquet(config.parquetFile.getPath)

def createFileWriter (path : String) = {
    val file = new java.io.File(path)
    file.getParentFile.mkdirs
    new java.io.FileWriter(file)
}

def prt(s: String, fw: java.io.FileWriter = null) = {
  println(s); 
  if(fw != null){ 
    fw.write(s + "\n"); fw.flush;
  }
}

val maxResults = 20;
val dimensions = List("server",
  //"responseInfo.contentPresent", "responseInfo.charset",
  //"agentInfo.isBot", "agentInfo.isProgram",
  //"agentInfo.bot", "agentInfo.program",
  //"requestInfo.url", "requestInfo.firstLevelPath",
  //"locationInfo.city", "locationInfo.country",
  //"entryType.database", "entryType.accession", 
  "clientInfo.ipAddress");


val metrics = List(
  ("server_hits", count("*") as "server_hits"),
  ("server_throughput", sum("responseInfo.contentSize") as "server_throughput"),
  ("server_distinct-ips", countDistinct("clientInfo.ipAddress") as "server_distinct-ips"))


val name = "oma"
val year = "2016"

metrics.foreach(m => {
  m match {
    case (metric, function) => {

      val fileName = name + "/" + year + "/" + m._1 
      val fw = createFileWriter(fileName + "/" + name + "-" + year + "-" + m._1 + ".tsv")

      prt(metric + "_per_months_ ################################# ")

      //Shows metrics by date
      df.groupBy($"month", $"year").agg(function).orderBy($"year" asc, $"month" asc).collect().foreach(r => prt(r.mkString("\t"), fw));


      //Shows metrics by dimensions
      val fwd = createFileWriter(fileName + "/" + name + "-" + year + "-" + m._1 + "-dimensions.tsv")
      dimensions.foreach(dimension => {
       prt("\n## Top_" + maxResults + "_" + metric + "_for_" + dimension + " ####")
       val data = df.groupBy($"""$dimension""").agg(function).orderBy($"""$metric""" desc).limit(maxResults).collect().foreach(r => prt(r.mkString("\t"), fwd))
      })
      fwd.close

      //Special case to show top entries by accession type
      /*List("trembl", "swissprot").foreach(db => {
        prt("\nTop_" + maxResults + "_" + metric + "_for_proteins_in_entryType.database_=_" + db + " ############");
        val data = df
          .filter($"entryType.database" === db)
          .groupBy($"entryType.accession")
          .agg(function)
          .orderBy($"""$metric""" desc).limit(maxResults).foreach(r => prt(r.mkString("\t")))
      })*/

      prt("\n");    
      fw.close

    }
  }
})

//df.groupBy($"month", $"year").agg(count("*") as "hits").orderBy($"month" asc, $"year" asc).show()
//df.groupBy($"""hostname""").agg(count("*") as "hits").orderBy($"hits" desc).limit(20).show()

