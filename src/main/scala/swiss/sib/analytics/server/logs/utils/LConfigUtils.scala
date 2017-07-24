package swiss.sib.analytics.server.logs.utils

import net.jcazevedo.moultingyaml.DefaultYamlProtocol
import net.jcazevedo.moultingyaml.PimpedString // if you don't supply your own protocol

import scala.util.matching.Regex
import java.io.File
import java.io.PrintWriter

case class YamlConfig(name: String, logDirectory: String, parquetFile: String, firstLevelPathFilter: Option[String])

object ConfigYamlProtocol extends DefaultYamlProtocol {
  implicit val configFormat = yamlFormat4(YamlConfig)
}

object LConfigUtils {

  def convertYamlToLConfig(yc: YamlConfig): LConfig = {

    val config = new LConfig(yc.name, new File(replaceEnvVariable(yc.logDirectory)), new File(replaceEnvVariable(yc.parquetFile)), yc.firstLevelPathFilter)

    println("Config: " + config)
    config;

  }

  def replaceEnvVariable(s: String): String = {

    val regex = """(\$\w+)(.*)""".r
    s match {
      case regex (variableName, _ ) => {
        val variableValue = System.getenv(variableName);
        val result = s.replace(variableName, variableValue);
        result
      }
      case _ => s;
    }
  }

  def readConfigFile(configFile: String): LConfig = {
    import ConfigYamlProtocol._
    convertYamlToLConfig(scala.io.Source.fromFile(configFile).mkString.parseYaml.convertTo[YamlConfig])
  }

}