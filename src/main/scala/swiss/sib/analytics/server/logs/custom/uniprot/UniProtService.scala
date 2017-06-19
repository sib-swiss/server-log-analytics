package swiss.sib.analytics.server.logs.custom.uniprot

object UniProtService {

  val ENTRY_PATH_PATTERN = """(\/uniprot\/)(\w{4,15})(.*)?""".r
  val swissProtEntries = scala.io.Source.fromFile("swissprot-entries.txt").getLines().toSet

  def getEntryAccessionProperty(endpoint: String): UniProtEntryPropery = {
    if (!endpoint.contains("query") && !endpoint.contains("opensearch")) {
      endpoint match {
        case ENTRY_PATH_PATTERN(_, entry, _) => {

          if (entry.toUpperCase().equals(entry)) { //Usually accessions are only upper case

            if (swissProtEntries.contains(entry)) {
              return UniProtEntryPropery(entry, "swissprot");
            } else return UniProtEntryPropery(entry, "trembl");
          } else {
            return UniProtEntryPropery(entry, "N/A");
          }
        }
        case _ => return UniProtEntryPropery("N/A", "N/A");
      }
    } else return UniProtEntryPropery("N/A", "N/A")
  }

}