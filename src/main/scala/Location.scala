/**
 * Location describes the climate of a specific location (undifferentiated)
 *
 * Parameters:
 *  - code: 3 letter location's identifier- example: "SYD"
 *  - zoneId: timezone identifier - example: "Australia/Sydney"
 *
 * Superclass parameters:
 *  - baseTemp: base temperature at the equator (latitude 0)
 *  - latitude: degrees with decimal notation
 *  - longitude: degrees with decimal notation
 *  - altitude: in meters from sea level
 */

package org.bom.weather

import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.HOURS
import java.time.{ZoneId, OffsetDateTime}

class Location(val code: String,
               val zoneId: String,
                   baseTemp: Double,
               val latitude: Double,
               val longitude: Double,
               val altitude: Int)
  extends Climate(baseTemp, latitude, longitude, altitude)
  with WeatherInfo with Simulated
{
  def currWeather: WeatherData = WeatherData(
    this,
    OffsetDateTime.now(ZoneId.of(zoneId)),
    "Sunny", // FALTA: generateCondition()
    temperature,
    pressure,
    75 // FALTA: generateHumidity()
  )

  def simulate(from: OffsetDateTime = OffsetDateTime.now(ZoneId.of(zoneId)))
              (repetitions: Int)
              (step: ChronoUnit = HOURS): Vector[WeatherData] = {
    def _simulate(from: OffsetDateTime,
                  repetitions: Int,
                  step: ChronoUnit,
                  acc: Vector[WeatherData]): Vector[WeatherData] = {
      if (repetitions == 0) acc else
        _simulate(from.plus(1, step), repetitions - 1, step, currWeather +: acc)
    }
    // Start simulation with empty result series
    if (repetitions < 0) Vector() else _simulate(from, repetitions, step, Vector())
  }

  override def simulatePeriod
  (from: OffsetDateTime = OffsetDateTime.now(ZoneId.of(zoneId)))
  (to: OffsetDateTime)
  (step: ChronoUnit = HOURS): Vector[WeatherData] =
  {
    simulate(from)(step.between(from, to).toInt)(step)
  }

}
