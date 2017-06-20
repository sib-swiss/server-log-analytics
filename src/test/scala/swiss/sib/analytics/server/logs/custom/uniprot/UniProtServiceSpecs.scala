package swiss.sib.analytics.server.logs.custom.uniprot

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class UniprotServiceSpecs extends FlatSpec with Matchers {

  "UniprotService" should "return swissprot for any entry matchign swissprot" in {

    val uep = UniProtService.getEntryAccessionProperty("/uniprot/P01308")
    uep.accession should equal("P01308")
    uep.database should equal("swissprot")

  }

  it should "should return trembl for any non matching swissprot entry" in {

    //any other entry is classified as Trembl
    val uep2 = UniProtService.getEntryAccessionProperty("/uniprot/PAAAAA")
    uep2.accession should equal("PAAAAA")
    uep2.database should equal("trembl")

  }

  it should "should return N/A for any URL that is not valid" in {

    //any other entry is classified as Trembl
    val uep2 = UniProtService.getEntryAccessionProperty("/unipr/PAAAAA")
    uep2.accession should equal("N/A")
    uep2.database should equal("N/A")

  }

}
