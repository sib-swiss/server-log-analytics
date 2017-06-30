package swiss.sib.analytics.server.logs.utils

import net.jcazevedo.moultingyaml.DefaultYamlProtocol
import net.jcazevedo.moultingyaml.PimpedString // if you don't supply your own protocol

import scala.util.matching.Regex
import java.io.File
import java.io.PrintWriter

case class YamlConfig(name: String,
                      logDirectory: String,
                      parquetFile: String)

object ConfigYamlProtocol extends DefaultYamlProtocol {
  implicit val configFormat = yamlFormat3(YamlConfig)
}

object LConfigUtils {

  def convertYamlToLConfig(yc: YamlConfig): LConfig = {

    val config = new LConfig(
      yc.name,
      new File(yc.logDirectory),
      new File(yc.parquetFile))

    println("Config: " + config)
    config;

  }

  def readConfigFile(configFile: String): LConfig = {
    import ConfigYamlProtocol._
    convertYamlToLConfig(scala.io.Source.fromFile(configFile).mkString.parseYaml.convertTo[YamlConfig])
  }

}