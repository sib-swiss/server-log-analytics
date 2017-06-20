package swiss.sib.analytics.server.logs.model

import swiss.sib.analytics.server.logs.custom.uniprot.UniProtEntryPropery

case class LogEntry(
  day: Integer,
  month: Integer,
  year: Integer,
  server: String, //Usually the server where the apache service run
  clientInfo: LogClientInfo,
  //locationInfo: LogLocationInfo, 
  requestInfo: LogRequestInfo,
  responseInfo: LogResponseInfo,
  referer: String,
  agentInfo: LogAgentInfo) {}
