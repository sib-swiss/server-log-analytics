package swiss.sib.analytics.server.logs.services

import swiss.sib.analytics.server.logs.model.LogLocationInfo
import swiss.sib.analytics.server.logs.utils.LConfig

/**
 * Service used to retrieve location information based on IP addresses
 */
object LocationService {

  val INTERNAL_IPS_PATTERN = """(^127\.)|(^10\.)|(^172\.1[6-9]\.)|(^172\.2[0-9]\.)|(^172\.3[0-1]\.)|(^192\.168\.)""".r

  val ipToCountries: scala.collection.mutable.Map[String, String] = new scala.collection.mutable.HashMap();
  val ipToCities: scala.collection.mutable.Map[String, String] = new scala.collection.mutable.HashMap();

  def getCountryAndCity(locationServiceUrl: String, ipAddress: String): LogLocationInfo = {

        if (!ipToCountries.contains(ipAddress)) {

          try {
            val jsonString = scala.io.Source.fromURL(locationServiceUrl + ipAddress).mkString
            val json = scala.util.parsing.json.JSON.parseFull(jsonString);

            json match {
              case Some(e: Map[String, String]) => {
                ipToCountries.put(ipAddress, e.getOrElse("country_name", "N/A country"))
                ipToCities.put(ipAddress, e.getOrElse("city", "N/A city"))
              }
              case _ => {
                ipToCountries.put(ipAddress, "N/A country")
                ipToCities.put(ipAddress, "N/A city")
              }
            }

          } catch {
            case e: Exception => {
              ipToCountries.put(ipAddress, "unknown-ip")
              ipToCities.put(ipAddress, "unknown-ip")
            }
          }

        }

        LogLocationInfo(ipToCountries.getOrElse(ipAddress, "unknown-ip"), ipToCities.getOrElse(ipAddress, "unknown-ip"))

  }

}
