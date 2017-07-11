package swiss.sib.analytics.server.logs.utils

object AgentUtils {

  val BOT_PATTERN = """(.*)(?i)(googlebot|yandexbot|baidupsider|bot|crawler|spider|pingdom|monitis|teoma|facebook|bing|yahoo|embedly|quora|outbrain|pinterest|vkshare|validator)(.*)""".r

  def getBotInfo(agent: String): (String, Boolean) = {
    agent match {
      case BOT_PATTERN(_, bot, _) => return (bot.toLowerCase().trim(), true);
      case _                      => return ("not-bot-pattern", false);
    }
  }

  val PROGRAM_PATTERN = """(.*)(?i)(python|java|perl|wget|ruby|curl|php)(.*)""".r

  def getProgramInfo(agent: String): (String, Boolean) = {
    agent match {
      case PROGRAM_PATTERN(_, program, _) => return (program.toLowerCase().trim(), true);
      case _                              => return ("not-programmatic-pattern", false);
    }
  }
}