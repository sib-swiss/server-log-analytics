import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

//Can not initialize counter due to context is not a instance of TaskInputOutputContext
//Logger.getLogger("parquet").setLevel(Level.OFF);

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

val name = scala.io.StdIn.readLine("Resource name in Insights (ex. STRING) : ")
val year = scala.io.StdIn.readLine("Year (ex. 2016) : ").toInt

val df = sqlContext.read.parquet(config.parquetFile.getPath).filter($"year" === year)

def createFileWriter (path : String) = {
    val file = new java.io.File(path)
    file.getParentFile.mkdirs
    new java.io.FileWriter(file, true)
}

def prt(s: String, fw: java.io.FileWriter = null) = {
  println(s); 
  if(fw != null){ 
    fw.write(s + "\n"); fw.flush;
  }
}

val maxResults = 100;
/*val dimensions = List("server",
  //"responseInfo.contentPresent", 
  "responseInfo.charset",
  //"agentInfo.isBot", "agentInfo.isProgram",
  //"agentInfo.bot", "agentInfo.program",
  //"requestInfo.url", "requestInfo.firstLevelPath",
  //"locationInfo.city", "locationInfo.country",
  //"entryType.database", "entryType.accession", 
  "clientInfo.ipAddress");
*/

val metrics = List(
  ("server_hits", count("*") as "server_hits", 
	List(("responseInfo.contentPresent", "content_length_present"), 
            ("agentInfo.isBot", "bot_traffic"), ("agentInfo.bot", "bot"),
            ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"),  
	    ("agentInfo.isProgram", "programmatic_access"), ("agentInfo.program", "programmatic"),
            ("responseInfo.charset", "charset"), ("responseInfo.status", "status_code"),
            ("clientInfo.ipAddress", "top_ips")))

  ,  
  
  ("server_throughput", sum("responseInfo.contentSize") as "server_throughput",
	List(("agentInfo.isBot", "bot_traffic"), ("agentInfo.bot", "bot"),
	    ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"),	
	    ("agentInfo.isProgram", "programmatic_access"), ("agentInfo.program", "programmatic"),
            ("responseInfo.charset", "charset"), ("responseInfo.status", "status_code"), 
	    ("clientInfo.ipAddress", "top_ips")))

  ,
  
  ("server_distinct_ips", countDistinct("clientInfo.ipAddress") as "server_distinct_ips",
	List(("agentInfo.isBot", "bot_traffic")))
  

)

metrics.foreach(m => {
  m match {
    case (metric, function, dimensions) => {

      val fileName = name + "/" + year + "/" + m._1 
      val fw = createFileWriter(fileName + "/" + name + "-" + year + "-" + m._1 + ".tsv")

      prt(metric + "_per_months_ ################################# ")
	
      //Shows metrics by date
      val total : Number = df.groupBy($"year").agg(function).first().get(1).asInstanceOf[Number]
      df.groupBy($"month", $"year").agg(function).orderBy($"year" asc, $"month" asc).collect().foreach(r => prt(r.mkString("\t"), fw));

      //Shows metrics by dimensions
      val fwd = createFileWriter(fileName + "/" + name + "-" + year + "-" + m._1 + "-dimensions.tsv")
      dimensions.foreach(d => {
       d match { 
	case (dimension, dimensionAlias) => {
       		prt("\n## Top_" + maxResults + "_" + metric + "_for_" + dimension + " ####")
       		val data = df.groupBy($"""$dimension""")
		    .agg(function).orderBy($"""$metric""" desc)
		    .limit(maxResults).collect()
		    .foreach(r => {	
				val lvalue : Number = r.get(1).asInstanceOf[Number]
				val percentage = (((lvalue.doubleValue * 10000) / total.longValue).toInt / 100.0)
				prt(dimensionAlias + "\t" + r.get(0) + "\t" + percentage, fwd)
				
		     })}}})
      fwd.close

      prt("\n");    
      fw.close

    }
  }
})

//df.groupBy($"month", $"year").agg(count("*") as "hits").orderBy($"month" asc, $"year" asc).show()
//df.groupBy($"""hostname""").agg(count("*") as "hits").orderBy($"hits" desc).limit(20).show()

