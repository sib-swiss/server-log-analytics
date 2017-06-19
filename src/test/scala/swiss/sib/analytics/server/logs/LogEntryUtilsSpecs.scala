package swiss.sib.analytics.server.logs

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import swiss.sib.analytics.server.logs.utils.LogEntryUtils

class LogEntryUtilsSpecs extends FlatSpec with Matchers {
  "LogEntryUtils" should "parse correctly log entries" in {
    
    val l1 = """elixir.org 127.0.0.1 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 200 1519 "-" "-" 1.358 - text/plain;charset=utf-8 127.0.0.2 5"""
    val l2 = """elixir.org 127.0.0.1 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 200 1871 "www.google.com" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36" 0.001 - image/png 127.0.0.2 -"""
    val l3 = """elixir.org 127.0.0.1 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 304 - "http://www.google.com" "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko" 0.000 - - 127.0.0.2 -"""
    val l4 = """elixir.org 127.0.0.1 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 304 - "-" "wget" 0.004 - text/html;charset=ISO-8859-1 180.76.15.142 1""" 
    val l5 = """elixir.org 127.0.0.1 - - [22/Mar/2017:11:09:30 +0000] "GET /foo/bar HTTP/1.1" 304 - "http://www.google.com" "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko" 0.000 - - 127.0.0.2 -"""
    val l6 = """elixir.org 127.0.0.1 - - [01/Mar/2016:17:33:25 -0500] "GET /foo/bar HTTP/1.1" 301 - "-" "Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)" 752 - text/plain 127.0.0.1 -"""
    val l7 = """elixir.org 127.0.0.1 - - [07/Sep/2016:08:25:27 +0000] "GET /index.php HTTP/1.1" 404 14858 "-" "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1" 0.030 86a04a24c8a28313d724f7d18c176f84 - 127.0.0.1 -"""
    val l8 = """elixir.org 127.0.0.1 - - [24/May/2016:23:35:23 +0000] " /images/arrow.png HTTP/1.1" 501 1139 "http://www.google.com" "Mozilla/5.0 (Windows NT 6.1; rv:46.0) Gecko/20100101 Firefox/46.0" 0.009 - - 190.80.8.31 -"""
    val l9 = """elixir.org 127.0.0.1 - - [26/Mar/2016:18:43:06 +0000] "GET /administrator/index.php HTTP/1.1" 404 14727 "-" "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1" 0.035 at7ov105990vb9qpqflik0rd87 - 127.0.0.1 -"""
    
    val logEntries = List(l1, l2, l3, l4, l5, l6, l7, l8, l9).map(LogEntryUtils.parseLogLine)
    
    val logEntry1 = logEntries(0)
    logEntry1.hostname should equal("elixir.org")
    logEntry1.clientInfo.ipAddress should equal("127.0.0.1")
    logEntry1.month should equal(3)
    logEntry1.responseInfo.contentSize should equal(1519)
    logEntry1.requestInfo.firstLevelPath should equal ("/foo")

    //TODO 1.358 is this the time in seconds?
    //TODO logEntry1.ip2 what is this IP?
    //TODO logEntry1.number what is this number
    
  }
}