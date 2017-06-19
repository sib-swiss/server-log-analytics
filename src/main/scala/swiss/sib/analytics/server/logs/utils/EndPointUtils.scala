package swiss.sib.analytics.server.logs.utils

object EndPointUtils {

  val FIRST_LEVEL_PATH_PATTERN = """(\/\w*)(\/)?(.*)?""".r

  def getFirstLevelPath(endpoint: String): String = {
    endpoint match {
      case FIRST_LEVEL_PATH_PATTERN(firstLevelPath, _, _) => return firstLevelPath;
      case _ => return "not-defined";
    }
  }
}