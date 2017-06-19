package org.elixir.insights.server.logs.utils

import org.scalatest._
import swiss.sib.analytics.server.logs.utils.LConfigUtils

class ConfigUtilsTest extends FlatSpec with Matchers {
  
  "ConfigUtilsTest" should "parse correctly a config file" in {
    
    val config = LConfigUtils.readConfigFile("src/test/resources/test-config.yaml");

    config.name should be ("Test Log Analytics");
    config.logDirectory.getPath should be ("/src/test/resources/logs/**");
    config.parquetFile.getPath should be ("/src/test/resources/log-parquet");
    
  }
  
}