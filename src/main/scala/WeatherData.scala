/**
 * WeatherData describes the data associated with a weather measurement
 */

package org.bom.weather

import java.time.{ZoneId, Duration, OffsetDateTime}
import java.time.temporal.ChronoUnit.SECONDS

case class WeatherData(location: Location,
                       localTime: OffsetDateTime,
                       condition: (String, Duration),
                       temperature: Double,
                       pressure: Double,
                       humidity: Int)
{
  override def toString: String = {
    val descriptor = if (condition._1 == "Clear") {
      if (location.isDay(localTime)) "Sun" else condition._1
    } else if (condition._1 == "Rain") {
      if (temperature < 0.0) "Snow" else condition._1
    } else condition._1

    s"${location.code}|${location.latitude},${location.longitude}|" +
      f"${localTime.truncatedTo(SECONDS).atZoneSameInstant(ZoneId.of("Z"))}|" +
      f"$descriptor|$temperature%1.1f|$pressure%1.1f|$humidity"
  }
}
