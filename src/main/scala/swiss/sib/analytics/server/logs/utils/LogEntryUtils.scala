package swiss.sib.analytics.server.logs.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import swiss.sib.analytics.server.logs.model.LogAgentInfo
import swiss.sib.analytics.server.logs.model.LogClientInfo
import swiss.sib.analytics.server.logs.model.LogEntry
import swiss.sib.analytics.server.logs.model.LogRequestInfo
import swiss.sib.analytics.server.logs.model.LogResponseInfo
import io.thekraken.grok.api.Grok

object LogEntryUtils {


  val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

  val CONTROL_CHARS_PATTERN = """[\p{C}]"""
  val PATTERN = """^(\S+) (\S+) (\S+) (\S+) \[([\w:\/]+\s[+\-]\d{4})\] "(.*) (\S+) (.*)" (\d{3}) (\S+) "(.*)" "(.*)" (\S+) (.*) (\S+) (\S+) (\S+)""".r

  def parseLogLine(log: String): LogEntry = {

    val logFileWithoutControlChars = log.replaceAll(CONTROL_CHARS_PATTERN, "")
    
    logFileWithoutControlChars match {
      case PATTERN(hostname, ipAddress, clientIdentd, userId, dateTime, method, endpoint, protocol, responseCode, contentSize, referer, agent, _, _, charset, _, _) => {
        try {

          //val locationInfo = LocationService.getCountryAndCity(ipAddress);
          val botInfo = AgentUtils.getBotInfo(agent);
          val programInfo = AgentUtils.getProgramInfo(agent);

          val agentInfo = LogAgentInfo(agent, botInfo._1, botInfo._2, programInfo._1, programInfo._2)

          val d = LocalDateTime.parse(dateTime, formatter);
          val contentPresent = !(contentSize.equals("-"))
          val content = if (contentPresent) contentSize.toLong else 0

          val responseInfo = LogResponseInfo(responseCode.toInt, content, contentPresent, charset)

          val logClientInfo = LogClientInfo(ipAddress, clientIdentd, userId)

          val requestInfo = LogRequestInfo(method, endpoint, protocol, EndPointUtils.getFirstLevelPath(endpoint))

          
          LogEntry(
            d.getDayOfMonth, 
            d.getMonthValue, 
            d.getYear,
            hostname,
            logClientInfo,
            //locationInfo, 
            requestInfo,
            responseInfo,
            referer,
            agentInfo)

        } catch {
          case e: Exception => throw new RuntimeException(s"""Failed to convert log line: $log""")
        }
      }
      case _ => throw new RuntimeException(s"""Cannot parse log line: $log""")
    }
  }
}
