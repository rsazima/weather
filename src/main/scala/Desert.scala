/**
 * Desert describes the special case of a desert location
 */

package org.bom.weather

import java.time.{Duration, OffsetDateTime}
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.HOURS

import org.bom.weather.seasons.{WINTER, AUTUMN, SPRING, SUMMER}

import scala.util.Random

case class Desert(override val code: String,
                  override val zoneId: String,
                               baseTemp: Double,
                               avgTempDelta: Double,
                  override val latitude: Double,
                  override val longitude: Double,
                  override val altitude: Int)
  extends Location(code, zoneId, baseTemp, avgTempDelta, latitude, longitude, altitude)
{
  /** Climatology */
  // Desert temperature amplitude is specially large
  override def temperature(dateTime: OffsetDateTime): Double = {
    val daytimeDelta = (2 * avgTempDelta / 3) * (daytimeTempFactor(dateTime) * 2.75)
    val seasonFactor = season(dateTime) match {
      case SPRING => season(dateTime).seasonTempFactor * 1.4
      case SUMMER => season(dateTime).seasonTempFactor * 2.4
      case AUTUMN => season(dateTime).seasonTempFactor * 0.7
      case WINTER => season(dateTime).seasonTempFactor * 0.45
    }

    ((latTempDelta(AltTempDelta(baseTemp)) + baseTemp * seasonFactor) / 2) + daytimeDelta
  }

  override def condition(lastCondition: Option[(String, Duration)] = None,
                step: ChronoUnit = HOURS): (String, Duration) = {
    def rand(l: List[String]) = Random.shuffle(l).head
    def randomCondition: (String, Duration) = (rand(List("Clear", "Clear", "Clear",
      "Clear", "Clouds")), Duration.ofHours(0))
    val inc: Long = (scala.util.Random.nextInt(8)).toLong

    lastCondition.map(c => (c._1, c._2.plus(inc, step))).map {
        case ("Clear", dur) =>
          if (dur.compareTo(Duration.ofDays(10)) > 0)
            (rand(List("Clear", "Clouds")), Duration.ofHours(0))
          else ("Clear", dur.plus(Duration.of(1, step)))
        case ("Clouds", dur) =>
          if (dur.compareTo(Duration.ofDays(2)) > 0)
            (rand(List("Clear", "Clouds", "Rain")), Duration.ofHours(0))
          else ("Clouds", dur.plus(Duration.of(1, step)))
        case ("Rain", dur) =>
          if (dur.compareTo(Duration.ofDays(1)) > 0)
            (rand(List("Clear", "Clouds")), Duration.ofHours(0))
          else ("Rain", dur.plus(Duration.of(1, step)))
        case _ => randomCondition
    }.getOrElse(randomCondition)
  }

  override def humidity(currCondition: (String, Duration) = condition()): Int =
    (currCondition match {
      case ("Clear", dur) => dur.toDays match {
        case 0 => 35; case 1 | 2 => 30; case 3 | 4 => 25; case _ => 20
      }
      case ("Clouds", dur) => if (dur.toDays < 2) 30 else 40
      case ("Rain", dur) => if (dur.toDays < 2) 65 else 85
      case _ => 35
    }) - scala.util.Random.nextInt(5)

}
