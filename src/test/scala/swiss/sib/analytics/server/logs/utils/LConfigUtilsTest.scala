package swiss.sib.analytics.server.logs.utils

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ConfigUtilsTest extends FlatSpec with Matchers {
  
  "ConfigUtilsTest" should "parse correctly a config file" in {
    
    val config = LConfigUtils.readConfigFile("src/test/resources/test-config.yaml");

    config.name should be ("Test Log Analytics");
    config.logDirectory.getPath should be ("/src/test/resources/logs/**");
    config.parquetFile.getPath should be ("/src/test/resources/log-parquet");
    
  }
  
}