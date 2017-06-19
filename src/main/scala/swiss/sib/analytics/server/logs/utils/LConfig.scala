package swiss.sib.analytics.server.logs.utils

import scala.util.matching.Regex
import java.io.File

case class LConfig(name: String, 
                  logDirectory: File,
                  parquetFile: File,
                  botPattern: Regex,
                  programPattern: Regex)