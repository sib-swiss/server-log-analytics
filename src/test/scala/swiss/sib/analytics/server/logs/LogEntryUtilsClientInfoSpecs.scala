package swiss.sib.analytics.server.logs

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import swiss.sib.analytics.server.logs.utils.LogEntryUtils

class LogEntryUtilsClientInfoSpecs extends FlatSpec with Matchers {

  "LogEntryUtilsClientInfoSpecs" should "correctly determine whether an IP is public or private" in {

    //Sample of a UniProt log file (starting with 0)
    val l0 = """uniprot-lb1.org 192.168.0.10 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 200 1519 "-" "-" 1.358 - text/plain;charset=utf-8 127.0.0.2 5"""
    val l1 = """uniprot-lb1.org 10.0.0.1 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 200 1871 "www.google.com" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36" 0.001 - image/png 127.0.0.2 -"""
    val l2 = """uniprot-lb2.org 66.55.66.77 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 304 - "http://www.google.com" "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko" 0.000 - - 127.0.0.2 -"""

    val le = List(l0, l1, l2).map(LogEntryUtils.parseLogLine)

    le(0).clientInfo.isPublic should be(false)
    le(1).clientInfo.isPublic should be(false)
    le(2).clientInfo.isPublic should be(true)

  }

}