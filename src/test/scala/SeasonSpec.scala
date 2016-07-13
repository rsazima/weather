/**
 * FlatSpec for Season
 */

import org.scalatest._
import java.time.ZoneId
import org.bom.weather.seasons._
import org.bom.weather.LocationFixtures

class SeasonSpec extends FlatSpec with Matchers with LocationFixtures
{
  /** Season interface */

  "Hemisphere" should "be NORTH for latitudes >= 0" in {
    SPRING.Hemisphere.forLat(0) should be (SPRING.Hemisphere.NORTH)
    SPRING.Hemisphere.forLat(45) should be (SPRING.Hemisphere.NORTH)
  }
  it should "be SOUTH for latitudes < 0" in {
    SPRING.Hemisphere.forLat(-45) should be (SPRING.Hemisphere.SOUTH)
  }

  "Season dates" should "be tested"
  it should "not change for spring" in {
    val springBoundaries = List(
      (45, date("2016-02-29T23:59:59Z")), (45, date("2016-03-01T00:00:00Z")),
      (45, date("2016-05-31T23:59:59Z")), (45, date("2016-06-01T00:00:00Z")),
      (-45, date("2016-08-31T23:59:59Z")), (-45, date("2016-09-01T00:00:00Z")),
      (-45, date("2016-11-30T23:59:59Z")), (-45, date("2016-12-01T00:00:00Z"))
    )
    springBoundaries.map { case (l, d) => SPRING.isCurrent(l, d) } should
      be(List(false, true, true, false, false, true, true, false))
  }

  it should "not change for summer" in {
    val summerBoundaries = List(
      (45, date("2016-05-31T23:59:59Z")), (45, date("2016-06-01T00:00:00Z")),
      (45, date("2016-08-31T23:59:59Z")), (45, date("2016-09-01T00:00:00Z")),
      (-45, date("2016-11-30T23:59:59Z")), (-45, date("2016-12-01T00:00:00Z")),
      (-45, date("2016-02-29T23:59:59Z")), (-45, date("2016-03-01T00:00:00Z"))
    )
    summerBoundaries.map { case (l, d) => SUMMER.isCurrent(l, d) } should
      be(List(false, true, true, false, false, true, true, false))
  }

  it should "not change for autumn" in {
    val autumnBoundaries = List(
      (45, date("2016-08-31T23:59:59Z")), (45, date("2016-09-01T00:00:00Z")),
      (45, date("2016-11-30T23:59:59Z")), (45, date("2016-12-01T00:00:00Z")),
      (-45, date("2016-02-29T23:59:59Z")), (-45, date("2016-03-01T00:00:00Z")),
      (-45, date("2016-05-31T23:59:59Z")), (-45, date("2016-06-01T00:00:00Z"))
    )
    autumnBoundaries.map { case (l, d) => AUTUMN.isCurrent(l, d) } should
      be(List(false, true, true, false, false, true, true, false))
  }

  it should "not change for winter" in {
    val winterBoundaries = List(
      (45, date("2016-11-30T23:59:59Z")), (45, date("2016-12-01T00:00:00Z")),
      (45, date("2016-02-29T23:59:59Z")), (45, date("2016-03-01T00:00:00Z")),
      (-45, date("2016-05-31T23:59:59Z")), (-45, date("2016-06-01T00:00:00Z")),
      (-45, date("2016-08-31T23:59:59Z")), (-45, date("2016-09-01T00:00:00Z"))
    )
    winterBoundaries.map{case (l,d) => WINTER.isCurrent(l,d)} should
      be (List(false, true, true, false, false, true, true, false))
  }

  /** Season companion object */

  "Season" should "provide a list of all seasons" in {
    Season.all should be (List(SPRING, SUMMER, AUTUMN, WINTER))
  }

  "The season in SYD on 12/JUL" should "be WINTER" in {
    Season(syd.latitude, july12SYD) should be (WINTER)
  }

  it should "then obviously NOT be SUMMER" in {
    SUMMER.isCurrent(syd.latitude, july12SYD) should not be true
  }

  "At the same time, in Pontianak and the Dead Sea" should "be SUMMER" in {
    Season(ptn.latitude, july12SYD.atZoneSameInstant(ZoneId.of(ptn.zoneId))
      .toOffsetDateTime) should be (SUMMER)
    Season(dse.latitude, july12SYD.atZoneSameInstant(ZoneId.of(dse.zoneId))
      .toOffsetDateTime) should be (SUMMER)
  }

  "No matter how you find out, seasons" should "be consistent all year round" in {
    Season(syd.latitude, july12SYD) should be (syd.season(july12SYD))
  }

  /** Season-specific properties */
  "daytimeOffsetFactor and seasonTempFactor" should "be consistent each season" in {
    SPRING.daytimeOffsetFactor should be <= 0.0
    SPRING.seasonTempFactor should be >= 1.0

    SUMMER.daytimeOffsetFactor should be <= SPRING.daytimeOffsetFactor
    SUMMER.seasonTempFactor should be >= SPRING.seasonTempFactor

    AUTUMN.daytimeOffsetFactor should be >= 0.0
    AUTUMN.seasonTempFactor should be <= 1.0

    WINTER.daytimeOffsetFactor should be >= AUTUMN.daytimeOffsetFactor
    WINTER.seasonTempFactor should be <= WINTER.seasonTempFactor
  }

}
