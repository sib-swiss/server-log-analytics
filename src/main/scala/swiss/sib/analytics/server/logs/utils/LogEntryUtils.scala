package swiss.sib.analytics.server.logs.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import swiss.sib.analytics.server.logs.model.LogAgentInfo
import swiss.sib.analytics.server.logs.model.LogClientInfo
import swiss.sib.analytics.server.logs.model.LogEntry
import swiss.sib.analytics.server.logs.model.LogRequestInfo
import swiss.sib.analytics.server.logs.model.LogResponseInfo

object LogEntryUtils {

  val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

  val CONTROL_CHARS_PATTERN = """[\p{C}]"""
  
  //Adapted from here: https://regex101.com/r/75x7uP/2/
  val PATTERN = """^(\S+ )?(\S+) (\S+) (\S+) \[([\w:\/]+\s[+\-]\d{4})\] "(\S+)?\s?([^"]+)?\s(\S+)?" (\d{3}|-) (Cache:\S+ )?(\d+|-)\s?"?([^"]*)"?\s?"?([^"]*)?"?(.*)""".r

  def parseLogLine(log: String): LogEntry = {

    val cleanedLogFile = cleanupLogEntry(log.replaceAll(CONTROL_CHARS_PATTERN, ""))

    cleanedLogFile match {
      case PATTERN(server, ipAddress, clientIdentd, userId, dateTime, method, endpoint, protocol, responseCode, _, contentSize, referer, agent, remaining) => {
        try {

          //val locationInfo = LocationService.getCountryAndCity(ipAddress);
          val botInfo = AgentUtils.getBotInfo(agent);
          val programInfo = AgentUtils.getProgramInfo(agent);

          val agentInfo = LogAgentInfo(agent, botInfo._1, botInfo._2, programInfo._1, programInfo._2)

          val d = LocalDateTime.parse(dateTime, formatter);
          val contentPresent = !(contentSize.equals("-"))
          val content = if (contentPresent) contentSize.toLong else 0

          val responseInfo = LogResponseInfo(responseCode.toInt, content, contentPresent, extractMimeType(remaining))

          val logClientInfo = LogClientInfo(ipAddress, clientIdentd, userId)

          val requestInfo = LogRequestInfo(method, endpoint, protocol, EndPointUtils.getFirstLevelPath(endpoint))

          LogEntry(
            d.getDayOfMonth,
            d.getMonthValue,
            d.getYear,
            if(server != null) server.trim() else "",
            logClientInfo,
            //locationInfo, 
            requestInfo,
            responseInfo,
            referer,
            agentInfo)

        } catch {
          case e: Exception => throw new RuntimeException(s"""Failed to convert log line: $cleanedLogFile""")
        }
      }
      case _ => throw new RuntimeException(s"""Cannot parse log line: $cleanedLogFile""")
    }
  }

  val MIME_TYPE_PATTERN = """(.*)(audio|video|text|application|image)\/(\w+)(.*)""".r
  def extractMimeType(text: String): String = {
    text match {
      case MIME_TYPE_PATTERN(_, first, second, _) => {
        return first + "/" + second;
      }
      case _ => "mime type not available"
    }
  }
  
  
  def cleanupLogEntry(text: String): String = {
    //In case of OMA log files
    return text.replaceAll("  \"", " \"").replace("\\\"", "'");
  }
}
