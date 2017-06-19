val GB_FACTOR = scala.math.pow(1024.0, 3);
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val df = sqlContext.read.parquet("/scratch/local/weekly/dteixeir/logs/prk/apache-logs.parquet")

val fw = new java.io.FileWriter("results.tsv")

def prt(s: String) = {
  println(s); fw.write(s + "\n"); fw.flush;
}

val maxResults = 20;
val dimensions = List("hostname",
  "responseInfo.contentPresent", "responseInfo.charset",
  "agentInfo.isBot", "agentInfo.isProgram",
  "agentInfo.bot", "agentInfo.program",
  "requestInfo.url", "requestInfo.firstLevelPath",
  //"locationInfo.city", "locationInfo.country",
  //"entryType.database", "entryType.accession", 
  "clientInfo.ipAddress");

val metrics = List(
  ("hits", count("*") as "hits"),
  ("throughput", sum("responseInfo.contentSize") as "throughput"),
  ("distinct-ips", countDistinct("clientInfo.ipAddress") as "distinct-ips"))

metrics.foreach(m => {
  m match {
    case (metric, function) => {

      prt(metric + "_per_months_ ################################# ")

      //Shows metrics by date
      df.groupBy($"month", $"year")
        .agg(function)
        .orderBy($"year" asc, $"month" asc).collect().foreach(r => prt(r.mkString("\t")));

      //Shows metrics by dimensions
      dimensions.foreach(dimension => {

        prt("\n## Top_" + maxResults + "_" + metric + "_for_" + dimension + " ####")

          val data = df.groupBy($"""$dimension""")
            .agg(function)
            .orderBy($"""$metric""" desc).limit(maxResults).collect().foreach(r => prt(r.mkString("\t")))

      })

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

    }
  }
})

//df.groupBy($"month", $"year").agg(count("*") as "hits").orderBy($"month" asc, $"year" asc).show()
//df.groupBy($"""hostname""").agg(count("*") as "hits").orderBy($"hits" desc).limit(20).show()

fw.close