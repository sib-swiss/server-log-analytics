import swiss.sib.analytics.server.logs._
import swiss.sib.analytics.server.logs.utils.LConfigUtils
import swiss.sib.analytics.server.logs.utils.LogEntryUtils

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val config = LConfigUtils.readConfigFile(System.getProperty("config.file"));

//Defines resource name
val name = config.name

val year = scala.io.StdIn.readLine("Year (ex. 2017) : ").toInt

val start = System.currentTimeMillis();

val dfForYear = sqlContext.read.parquet(config.parquetFile.getPath).filter($"year" === year)

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

val metrics = List(
      ("server_hits", false, count("*") as "server_hits",
        List(("responseInfo.contentPresent", "content_length_present"),
          ("agentInfo.isBot", "bot_traffic"), ("agentInfo.bot", "bot"), ("agentInfo.agent", "agent"),
          ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"),
          ("agentInfo.isProgram", "programmatic_access"), ("agentInfo.program", "programmatic"),
          ("responseInfo.charset", "charset"), ("responseInfo.status", "status_code"),
          ("clientInfo.ipAddress", "top_ips"))),

      ("server_hits_without_bots", true, count("*") as "server_hits_without_bots",
        List(("responseInfo.contentPresent", "content_length_present"),
          ("agentInfo.agent", "agent"),
          ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"),
          ("agentInfo.isProgram", "programmatic_access"), ("agentInfo.program", "programmatic"),
          ("responseInfo.charset", "charset"), ("responseInfo.status", "status_code"),
          ("clientInfo.ipAddress", "top_ips"))),

      ("server_throughput", false, sum("responseInfo.contentSize") as "server_throughput",
        List(("agentInfo.agent", "agent"),
          ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"),
          ("agentInfo.isProgram", "programmatic_access"), ("agentInfo.program", "programmatic"),
          ("responseInfo.charset", "charset"), ("responseInfo.status", "status_code"),
          ("clientInfo.ipAddress", "top_ips"))),

      ("server_throughput_without_bots", true, sum("responseInfo.contentSize") as "server_throughput_without_bots",
        List(("agentInfo.isBot", "bot_traffic"), ("agentInfo.bot", "bot"), ("agentInfo.agent", "agent"),
          ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"),
          ("agentInfo.isProgram", "programmatic_access"), ("agentInfo.program", "programmatic"),
          ("responseInfo.charset", "charset"), ("responseInfo.status", "status_code"),
          ("clientInfo.ipAddress", "top_ips"))),

      ("server_distinct_ips", false, countDistinct("clientInfo.ipAddress") as "server_distinct_ips",
        List(("agentInfo.isBot", "bot_traffic"), ("agentInfo.bot", "bot"), ("agentInfo.agent", "agent"), ("agentInfo.program", "programmatic"),
            ("responseInfo.status", "status_code"),
            ("requestInfo.firstLevelPath", "first_level_path"), ("requestInfo.url", "top_urls"))),

      ("server_distinct_ips_without_bots", true, countDistinct("clientInfo.ipAddress") as "server_distinct_ips_without_bots",
        List(("agentInfo.agent", "agent"),
          ("responseInfo.status", "status_code"),
          ("agentInfo.program", "programmatic"), ("requestInfo.firstLevelPath", "first_level_path"),
          ("requestInfo.url", "top_urls"))))
          
metrics.foreach(m => {
  m match {
    case (metric, filterBot, function, dimensions) => {

      val fileName = "insights-reports/" + name + "/" + year + "/" + metric 
      val fw = createFileWriter(fileName + "/" + name + "-" + year + "-" + metric + ".tsv")

      prt(metric + "_per_months_ ################################# ")
	
      val df = if(filterBot) {
        dfForYear.filter($"agentInfo.isBot" === false)
      }else dfForYear
        
      //Shows metrics by date
      val total : Number = df.groupBy($"year").agg(function).first().get(1).asInstanceOf[Number]
      df.groupBy($"month", $"year").agg(function).orderBy($"year" asc, $"month" asc).collect().foreach(r => prt(r.mkString("\t"), fw));
      //Per year
      df.groupBy($"year").agg(function).orderBy($"year" asc).collect().foreach(r => prt("ALL\t" + r.mkString("\t"), fw));
      fw.close

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
				val percentage = lvalue.doubleValue / total.longValue
				prt(dimensionAlias + "\t" + r.get(0) + "\t" + percentage + "\t" + r.get(1), fwd)
	     })}}})
      fwd.close

      prt("\n");    

    }
  }
})

println("Finished in " + (System.currentTimeMillis() - start) / (60 * 1000.0) + " min")

//df.groupBy($"month", $"year").agg(count("*") as "hits").orderBy($"month" asc, $"year" asc).show()
//df.groupBy($"""hostname""").agg(count("*") as "hits").orderBy($"hits" desc).limit(20).show()

