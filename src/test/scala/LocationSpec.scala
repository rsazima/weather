/**
 * FlatSpec for Location
 */

package org.bom.weather

import org.scalatest._
import java.time.{Duration, OffsetDateTime}
import java.time.temporal.ChronoUnit.{HOURS, DAYS, MILLIS}
import org.bom.weather.seasons._

class LocationSpec extends FlatSpec with Matchers with BeforeAndAfter
                                    with LocationFixtures
{
  /** Climate interface */

  "Temperature" should "vary with latitude" in {
    (1 to 10).map(_ => cpq.temperature(july12CPQ)).sum / 10 should
      be < (1 to 10).map(_ => rec.temperature(july12REC)).sum / 10
  }

  it should "vary with altitude" in {
    val sydHigh = new Location("SYD", "Australia/Sydney", 20.0, 8.0, -33.51, 151.12, 2000)
    (1 to 10).map(_ => sydHigh.temperature(july12SYD)).sum / 10 should
      be < (1 to 10).map(_ => syd.temperature(july12SYD)).sum / 10
  }

  it should "vary with season" in {
    ((1 to 10).map(_ => kln.temperature(date("2016-12-12T13:15:30+01:00"))).sum / 10) should
      be < ((1 to 10).map(_ => kln.temperature(july12KLN)).sum / 10)
  }

  it should "vary with daytime" in {
    ((1 to 10).map(_ => ptn.temperature(date("2016-07-12T01:15:30+07:00"))).sum / 10) should
      be < ((1 to 10).map(_ => ptn.temperature(july12PTN)).sum / 10)
  }

  "Pressure varies with altitude, so it" should
    "be higher than 1070.00 hPa at DeadSea/Jordan" in {
    dse.pressure should be > 1070.0
  }

  it should "be 1013.25 hPa at Pontianak/Indonesia" in {
    ptn.pressure should be (1013.25)
  }

  it should "be around 917 hPa at Campinas/Brazil" in {
    cpq.pressure.round should be (917)
  }

  "Sunrise & sunset" should "tell us if it's day or night" in {
    dse.isDay(OffsetDateTime.of(OffsetDateTime.now().toLocalDate,
      dse.sunrise(OffsetDateTime.now).minusMinutes(1), OffsetDateTime.now.getOffset)) should
        be (false)

    dse.isDay(OffsetDateTime.of(OffsetDateTime.now().toLocalDate,
      dse.sunrise(OffsetDateTime.now), OffsetDateTime.now.getOffset)) should
        be (true)

    dse.isDay(OffsetDateTime.of(OffsetDateTime.now().toLocalDate,
      dse.sunset(OffsetDateTime.now).minusMinutes(1), OffsetDateTime.now.getOffset)) should
        be (true)

    dse.isDay(OffsetDateTime.of(OffsetDateTime.now().toLocalDate,
      dse.sunset(OffsetDateTime.now), OffsetDateTime.now.getOffset)) should
        be (false)
  }

  it should "define the length of daytime & nighttime" in {
    kln.daytime(july12KLN).toMillis should
      be (Duration.between(kln.sunrise(july12KLN), kln.sunset(july12KLN)).toMillis)
    syd.nighttime(july12SYD) should
      be (Duration.ofHours(24).minus(syd.daytime(july12SYD)))
  }

  it should "vary with latitude" in {
    rec.sunrise(july12REC).isBefore(cpq.sunrise(july12CPQ))
    rec.sunset(july12REC).isAfter(cpq.sunset(july12CPQ))
  }

  it should "vary with season" in {
    syd.sunrise(date("2016-01-12T13:15:30+10:00")).isBefore(syd.sunrise(july12SYD))
    syd.sunset(date("2016-01-12T13:15:30+10:00")).isAfter(syd.sunset(july12SYD))
  }

  /** Location-specific weather */

  "Location" should "know its current season" in {
    syd.season(july12SYD) should be (WINTER)
  }

  "Locations in opposite hemispheres" should "should have mirror seasons" in {
    val dates = List(date("2016-01-12T13:15:30Z"), date("2016-04-12T13:15:30Z"),
                     date("2016-07-12T13:15:30Z"), date("2016-10-12T13:15:30Z"))
    dates.map(syd.season(_)).splitAt(2) should
      be (dates.map(dse.season(_)).splitAt(2).swap)
  }

  "Temperature for DeadSea/Jordan in July" should "be high" in {
    dse.temperature(july12DSE) should be > (25.0)
  }

  "Temperature for Pontianak/Indonesia" should "always be pleasant" in {
    ptn.temperature(date("2016-01-12T13:15:30+07:00")) should be > (20.0)
    ptn.temperature(date("2016-04-12T13:15:30+07:00")) should be > (20.0)
    ptn.temperature(date("2016-07-12T13:15:30+07:00")) should be > (20.0)
    ptn.temperature(date("2016-10-12T13:15:30+07:00")) should be > (20.0)
  }

  "Hourly temperatures during a day" should
    "vary according to the location's mean temperature range" in {
    val temps = (1 to 24).map(i => cpq.temperature(july12CPQ.plusHours(i)).toInt)
    temps.max - temps.min should be ((2 * 12)/3)
  }

  "Current condition" should "be random without previous history" in {
    (1 to 20).map(_ => syd.condition()).map(_._1).distinct should
      contain allOf ("Clear", "Clouds", "Rain")
  }

  "When informed, a previous condition" should "influence the next condition" in {
    cpq.condition(Some(cpq.condition(Some(cpq.condition(
      Some(("Clear", Duration.ofDays(0))))))))._1 should be ("Clear")
  }

  it should "not keep unchanged too long, though" in {
    def RecCond(c: (String, Duration), reps: Int,
                acc: List[(String, Duration)] = Nil): List[(String, Duration)] =
      if (reps == 0) acc else RecCond(syd.condition(Some(c), DAYS), reps - 1, c :: acc)

    RecCond(("Clear", Duration.ofDays(0)), 24).map(_._1).distinct.size should be > 1
  }

  "Humidity" should "depend on condition type and duration" in {
    (1 to 10).map( _ =>
      (1 to 10).map(_ => rec.humidity(rec.condition())).distinct.size
    ).sum / 10 should be > 5

    rec.humidity(("Clouds", Duration.ofDays(2))) should
      (be > (rec.humidity(("Clear", Duration.ofDays(2)))) and
        be < (rec.humidity(("Rain", Duration.ofDays(2)))))
  }

  "A location" should "provide its current weather data" in {
    cpq.currWeather()()() shouldBe a [WeatherData]
  }

  "A location's current weather" should "contain location info, " +
    "local time, current condition, temperature, pressure and humidity" in {
    val (temp, pres) = (syd.temperature(july12SYD), syd.pressure)
    val currWeather = syd.currWeather(july12SYD)()()
    currWeather should have (
      'location (syd),
      'localTime (july12SYD),
      'temperature (temp),
      'pressure (pres)
    )
    List(currWeather.condition._1) should contain oneOf ("Clear", "Clouds", "Rain")
    currWeather.humidity should be > 10
  }

  "Weather data" should "be output in the format " +
    "'COD|lat.lng|UTC datetime|condition|temperature|pressure|humidity'" in {
    val weather = syd.currWeather()()()
    weather.toString().filter(_ == '|').size should be(6)
    val infos = weather.toString().split('|')
    infos(0) should be (syd.code.toUpperCase)
    infos(1).split(',').map(_.toDouble) should be (Array(syd.latitude, syd.longitude))
    Duration.between(OffsetDateTime.parse(infos(2)), weather.localTime).getNano should
      be < 1000000000
    List(infos(3)) should contain oneOf ("Clear", "Clouds", "Rain")
    (infos(4).toDouble - weather.temperature).abs should be < 0.1
    (infos(5).toDouble - weather.pressure).abs should be < 0.1
    infos(6).toInt should be (weather.humidity)
  }

  /** Simulate weather for Locations */

  "A location" should "simulate a series of weather data for X repetitions" in {
    cpq.simulate()(10)() shouldBe a [Vector[_]]
    cpq.simulate()(0)().size should be (0)
    cpq.simulate()(10)().size should be (10)
  }

  "A location" should "simulate a series of weather data for a period P" in {
    cpq.simulatePeriod(from)(to)(HOURS) shouldBe a [Vector[_]]
    cpq.simulatePeriod(to)(from)(HOURS).size should be (0)
    cpq.simulatePeriod(to.minusHours(10))(to)(HOURS).size should be (10)
  }

  "A simulation" should "start on the informed date/time" in {
    (cpq.simulate(july12CPQ)(1)()).head.localTime should be (july12CPQ)
  }

  "A simulation" should "use the informed step unit" in {
    val x::(y::z) = cpq.simulate(july12CPQ)(2)(DAYS).toList
    Duration.between(x.localTime, y.localTime).toHours should be (24)
  }




}
