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

import java.time.MonthDay

abstract class Climate(baseTemp: Double,
                       latitude: Double,
                       longitude: Double,
                       altitude: Int)
{
  def temperature: Double = tempLatDelta(tempAltDelta(baseTemp))
  def pressure: Double = 1013.25 * math.exp(altitude / -7000.0) // p0 * e-(h/h0)

  protected def tempLatDelta(temp: Double): Double =
    temp - (0.3875 * math.abs(latitude))
  protected def tempAltDelta(temp: Double): Double =
    temp - (0.5475 * altitude / 100)
  protected def seasonsDates =
    if (latitude >= 0) Map("Spring" -> MonthDay.of(3,1), "Summer" -> MonthDay.of(6,1),
                           "Autumn" -> MonthDay.of(9,1), "Winter" -> MonthDay.of(12,1))
    else Map("Autumn" -> MonthDay.of(3,1), "Winter" -> MonthDay.of(6,1),
             "Spring" -> MonthDay.of(9,1), "Summer" -> MonthDay.of(12,1))
}
