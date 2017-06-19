package swiss.sib.analytics.server.logs.model

case class LogRequestInfo(method: String, url: String, protocol: String, firstLevelPath: String)
