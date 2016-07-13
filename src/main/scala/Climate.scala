/**
 * Climate describes an extremely simplified climate model
 *
 * Parameters:
 *  - baseTemp: base mean temperature
 *  - avgTempDelta: mean temperature range
 *  - latitude: degrees with decimal notation
 *  - longitude: degrees with decimal notation
 *  - altitude: in meters from sea level
 */

package org.bom.weather

import java.time.{Duration, LocalTime, OffsetDateTime}
import org.bom.weather.seasons._

abstract class Climate(baseTemp: Double,
                       avgTempDelta: Double,
                       latitude: Double,
                       longitude: Double,
                       altitude: Int)
{
  /** Climatology */
  // Temperature is influenced by angle of incidence (lat & season) of sunrays -
  // concentration in an area - rather than hours of light and distance from sun
  def temperature(dateTime: OffsetDateTime): Double = {
    val daytimeTempDelta = (2 * avgTempDelta / 3) * daytimeTempFactor(dateTime)

    ((latTempDelta(AltTempDelta(baseTemp)) + baseTemp *
      season(dateTime).seasonTempFactor) / 2) + daytimeTempDelta
  }

  def pressure: Double = 1013.25 * math.exp(altitude / -7000.0) // p0 * e-(h/h0)

  def season(dateTime: OffsetDateTime): Season

  /** Astronomy */
  def isDay(dateTime: OffsetDateTime): Boolean =
    dateTime.toLocalTime.compareTo(sunrise(dateTime)) >= 0 &&
      dateTime.toLocalTime.compareTo(sunset(dateTime)) < 0

  def sunrise(dateTime: OffsetDateTime): LocalTime = {
    val actSunrise = 6.0 + daytimeOffset(dateTime)  // 6am +- offset
    LocalTime.of(actSunrise.toInt, ((actSunrise % 1) * 60).toInt)
  }

  def sunset(dateTime: OffsetDateTime): LocalTime = {
    val actSunrise = 18.0 - daytimeOffset(dateTime) // 6pm +- offset
    LocalTime.of(actSunrise.toInt, ((actSunrise % 1) * 60).toInt)
  }

  def daytime(dateTime: OffsetDateTime): Duration =
    Duration.between(sunrise(dateTime), sunset(dateTime))

  def nighttime(dateTime: OffsetDateTime): Duration =
    Duration.ofHours(24).minus(daytime(dateTime))

  /** Helper methods */
  protected def daytimeSinceSunrise(dateTime: OffsetDateTime): Duration =
    Duration.between(sunrise(dateTime), dateTime)

  protected def nighttimeSinceSunset(dateTime: OffsetDateTime): Duration =
    if (dateTime.toLocalTime.isAfter(LocalTime.of(12,0)))
      Duration.between(sunset(dateTime), dateTime) else
      Duration.between(sunset(dateTime), dateTime).plus(Duration.ofHours(24))

  protected def daytimeTempFactor(dateTime: OffsetDateTime): Double = {
    // FALTA: considerar que pico é 14h, não ao anoitecer
    if (isDay(dateTime)) daytimeSinceSunrise(dateTime).toMillis.toDouble /
      daytime(dateTime).toMillis.toDouble
    else  1.0 - (nighttimeSinceSunset(dateTime).toMillis.toDouble /
      nighttime(dateTime).toMillis.toDouble)
  }

  // Latitude effect modelled as a circle of radius 6h (x^2 + y^2 = r^2)
  protected def daytimeOffset(dateTime: OffsetDateTime) = {
    // https://en.wikipedia.org/wiki/Sun_path & https://en.wikipedia.org/wiki/Daytime
    val quarterDay = 6.0 // hours
    val absOffset = quarterDay - math.sqrt(quarterDay*quarterDay -
        (latitude*latitude) / (15.0 * 15.0))
    absOffset * season(dateTime).daytimeOffsetFactor
  }

  protected def latTempDelta(temp: Double): Double =
    //temp - (0.3875 * math.abs(latitude))
    temp - (0.3 * math.abs(latitude))

  protected def AltTempDelta(temp: Double): Double =
    //temp - (0.5475 * altitude / 100)
    temp - (0.4 * altitude / 100)
}
