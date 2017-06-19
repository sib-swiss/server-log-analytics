package swiss.sib.analytics.server.logs.utils

object AgentUtils {

  val BOT_PATTERN = """(.*)(?i)(bot|crawler|spider|pingdom|monitis|teoma|facebook|bing|yahoo|embedly|quora|outbrain|pinterest|vkshare|validator)(.*)""".r

  def getBotInfo(agent: String): (String, Boolean) = {
    agent match {
      case BOT_PATTERN(_, bot, _) => return (bot, true);
      case _                      => return ("not-bot-pattern", false);
    }
  }

  val PROGRAM_PATTERN = """(.*)(?i)(python|java|perl|wget|ruby|curl)(.*)""".r

  def getProgramInfo(agent: String): (String, Boolean) = {
    agent match {
      case PROGRAM_PATTERN(_, program, _) => return (program, true);
      case _                              => return ("not-program-pattern", false);
    }
  }
}