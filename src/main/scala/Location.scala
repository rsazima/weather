/**
 * Location describes the climate of a specific location (undifferentiated)
 *
 * Parameters:
 *  - code: 3 letter location's identifier- example: "SYD"
 *  - zoneId: timezone identifier - example: "Australia/Sydney"
 *
 * Superclass parameters:
 *  - baseTemp: base mean temperature
 *  - avgTempDelta: mean temperature range
 *  - latitude: degrees with decimal notation
 *  - longitude: degrees with decimal notation
 *  - altitude: in meters from sea level
import org.bom.weather._
 */

package org.bom.weather

import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.HOURS
import java.time.{Duration, ZoneId, OffsetDateTime, LocalTime}
import org.bom.weather.seasons._
import scala.util.Random

class Location(val code: String,
               val zoneId: String,
                   baseTemp: Double,
                   avgTempDelta: Double,
               val latitude: Double,
               val longitude: Double,
               val altitude: Int)
  extends Climate(baseTemp, avgTempDelta, latitude, longitude, altitude)
  with Simulateable
{
  /** Climatology */
  def season(dateTime: OffsetDateTime = OffsetDateTime.now(ZoneId.of(zoneId))): Season =
    Season(latitude, dateTime)

  def condition(lastCondition: Option[(String, Duration)] = None,
                step: ChronoUnit = HOURS): (String, Duration) = {
    def rand(l: List[String]) = Random.shuffle(l).head
    def randomCondition: (String, Duration) =
      (rand(List("Clear", "Clouds", "Rain")), Duration.ofHours(0))
    val inc: Long = (scala.util.Random.nextInt(8)).toLong

    lastCondition.map(c => (c._1, c._2.plus(inc, step))).map {
        case ("Clear", dur) =>
          if (dur.compareTo(Duration.ofDays(5)) > 0)
            (rand(List("Clear", "Clouds")), Duration.ofHours(0))
          else ("Clear", dur.plus(Duration.of(1, step)))
        case ("Clouds", dur) =>
          if (dur.compareTo(Duration.ofDays(3)) > 0)
            (rand(List("Clear", "Clouds", "Rain")), Duration.ofHours(0))
          else ("Clouds", dur.plus(Duration.of(1, step)))
        case ("Rain", dur) =>
          if (dur.compareTo(Duration.ofDays(2)) > 0)
            (rand(List("Clear", "Clouds")), Duration.ofHours(0))
          else ("Rain", dur.plus(Duration.of(1, step)))
        case _ => randomCondition
    }.getOrElse(randomCondition)
  }

  def humidity(currCondition: (String, Duration) = condition()): Int =
    (currCondition match {
      case ("Clear", dur) => dur.toDays match {
        case 0 => 60; case 1 | 2 => 50; case 3 | 4 => 40; case _ => 30
      }
      case ("Clouds", dur) => if (dur.toDays < 2) 60 else 70
      case ("Rain", dur) => if (dur.toDays < 2) 85 else 99
      case _ => 55
    }) - scala.util.Random.nextInt(15)

  def currWeather(dateTime: OffsetDateTime = OffsetDateTime.now(ZoneId.of(zoneId)))
                 (lastCondition: Option[(String, Duration)] = None)
                 (step: ChronoUnit = HOURS): WeatherData = {
    val currCondition = condition(lastCondition, step)
    WeatherData(
      this,
      dateTime,
      currCondition,
      temperature(dateTime), // Improvements: condition to influence temperature
      pressure,              // Improvements: condition to influence temperature
      humidity(currCondition)// Improvements: consider time, season & temperature
    )
  }

  /** Simulation */
  def simulate(from: OffsetDateTime = OffsetDateTime.now(ZoneId.of(zoneId)))
              (repetitions: Int)
              (step: ChronoUnit = HOURS): Vector[WeatherData] = {
    def _simulate(from: OffsetDateTime,
                  repetitions: Int,
                  step: ChronoUnit,
                  acc: Vector[WeatherData]): Vector[WeatherData] = {
      if (repetitions == 0) acc else
        _simulate(from.plus(1, step), repetitions - 1, step, acc :+
          currWeather(from)(acc.lastOption.map(_.condition))(step))
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
