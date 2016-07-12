/**
 * Climate describes an extremely simplified climate model
 *
 * Parameters:
 *  - baseTemp: base temperature at the equator (latitude 0)
 *  - latitude: degrees with decimal notation
 *  - longitude: degrees with decimal notation
 *  - altitude: in meters from sea level
 */

package org.bom.weather

import java.time.{OffsetDateTime, MonthDay}

abstract class Climate(baseTemp: Double,
                       latitude: Double,
                       longitude: Double,
                       altitude: Int)
{
  def season(dateTime: OffsetDateTime): String
  def temperature: Double = tempLatDelta(tempAltDelta(baseTemp))
  def pressure: Double = 1013.25 * math.exp(altitude / -7000.0) // p0 * e-(h/h0)

  protected def tempLatDelta(temp: Double): Double =
    temp - (0.3875 * math.abs(latitude))
  protected def tempAltDelta(temp: Double): Double =
    temp - (0.5475 * altitude / 100)
}
