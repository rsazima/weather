/**
 * Example application with weather overview for locations
 */

import java.time.{Duration, OffsetDateTime}
import org.bom.weather._

object WeatherChannel extends App {
  def date(str: String) = OffsetDateTime.parse(str)

  val dec25MOW = date("2015-12-25T00:00:01+03:00")
  val dec25CGN = date("2015-12-25T00:00:01+01:00")
  val dec25AMM = date("2015-12-25T00:00:01+02:00")
  val dec25MIA = date("2015-12-25T00:00:01-05:00")
  val dec25PNK = date("2015-12-25T00:00:01+07:00")
  val dec25REC = date("2015-12-25T00:00:01-03:00")
  val dec25LPB = date("2015-12-25T00:00:01-04:00")
  val dec25VCP = date("2015-12-25T00:00:01-03:00")
  val dec25SYD = date("2015-12-25T00:00:01+10:00")
  val dec25CHC = date("2015-12-25T00:00:01+12:00")
  val dec25MHV = date("2015-12-25T00:00:01-08:00")

  val mow = new Location("MOW", "Europe/Moscow", 12.0, 8.0, 55.45, 37.37, 151)
  val cgn = new Location("CGN", "Europe/Berlin", 15.0, 9.0, 50.56, 6.57, 58)
  val amm = new Location("AMM", "Asia/Amman", 25.0, 12.0, 31.56, 35.47, -423)
  val mia = new Location("MIA", "America/New_York", 21.0, 15.0, 25.46, -80.12, 2)
  val pnk = new Location("PNK", "Asia/Pontianak", 22.0, 7.0, 0.0, 109.2, 0)
  val rec = new Location("REC", "America/Recife", 25.0, 7.5, -8.3, -34.54, 0)
  val lpb = new Location("LPB", "America/La_Paz", 15.0, 15.0, -16.3, -68.09, 3640)
  val vcp = new Location("VCP", "America/Sao_Paulo", 22.0, 12.0, -22.54, -47.03, 700)
  val syd = new Location("SYD", "Australia/Sydney", 20.0, 11.0, -33.51, 151.12, 19)
  val chc = new Location("CHC", "Pacific/Auckland", 14.0, 11.0, -43.31, 172.37, 6)
  val mhv = Desert("MHV", "America/Los_Angeles", 11.0, 12.0, 35.00, -115.28, 100)

  val weatherNetwork = Map[String, (Location, OffsetDateTime)](
      mow.code -> (mow, dec25MOW)
    , cgn.code -> (cgn, dec25CGN)
    , amm.code -> (amm, dec25AMM)
    , mia.code -> (mia, dec25MIA)
    , pnk.code -> (pnk, dec25PNK)
    , rec.code -> (rec, dec25REC)
    , lpb.code -> (lpb, dec25LPB)
    , vcp.code -> (vcp, dec25VCP)
    , syd.code -> (syd, dec25SYD)
    , chc.code -> (chc, dec25CHC)
    , mhv.code -> (mhv, dec25MHV)
  )

  def avgWeather(data: Vector[WeatherData]) = {
    def concat(wd1: ((String, Duration), Double, Double, Int),
               wd2: (List[(String, Duration)], List[Double], List[Double], List[Int])) =
      (wd1._1 :: wd2._1, wd1._2 :: wd2._2, wd1._3 :: wd2._3, wd1._4 :: wd2._4)
    val tupledData = data.map(_.getWeather)
    val avg = tupledData.foldRight((List[(String, Duration)](), List[Double](),
                                    List[Double](), List[Int]()))(concat(_, _))
    val mostCommonCond = avg._1.map(_._1).groupBy(identity).maxBy(_._2.size)._1
    val avgTemp = avg._2.sum / avg._2.size
    val avgPres = avg._3.sum / avg._3.size
    val avgHumi = avg._4.sum / avg._4.size

    (mostCommonCond, avgTemp, avgPres, avgHumi)
  }

  // data: hourly WeatherData
  // samplesPerDay: how many samples to keep for each 24h (1 to 24)
  def processHourlyData[T](data: Vector[WeatherData], samplesPerDay: Int = 4,
                           transf: Vector[WeatherData] => T) = {
    require((0 < samplesPerDay) && (samplesPerDay < 25), "Need to keep at least 1 sample out of 24")
    val dailyData = data.grouped(24).toList
    val dailySamples = dailyData.map(_.grouped(24 / samplesPerDay).toVector.map(_.head))
    dailySamples.map(transf(_))
  }

  val days = 360

  val hourlyData =
    weatherNetwork.par.mapValues(locDt => locDt._1.simulate(locDt._2)(days*24)())

  val dailyAvgs = hourlyData.mapValues(
    processHourlyData[(String, Double, Double, Int)](_, 24, avgWeather(_)))

  dailyAvgs.toMap.map { dailyAvgs =>
    val monthlyAvgs = dailyAvgs._2.grouped(30).map(x => avgWeather(x.map(tp =>
      WeatherData(syd, dec25SYD, (tp._1, Duration.ofDays(0)), tp._2, tp._3, tp._4)
    ).toVector))
    println(s"JAN-DEC weather for: ${dailyAvgs._1}\n" +
            s"AvgTemp\t| Press  \t| Hum\t| Cond\n" + monthlyAvgs.map(dw =>
        f"${dw._2}%1.1f   \t| ${dw._3}%1.1f \t| ${dw._4}\t| ${dw._1}\n").mkString)
  }

  val dailySamples = hourlyData
    .mapValues(processHourlyData[Vector[WeatherData]](_, 4, x => x))

  weatherNetwork.par.mapValues{ locDt =>
    val temps = (1 to 24).map(i => locDt._1.temperature(OffsetDateTime.now.plusHours(i)))
    (temps.min, temps.max)
  }.map(lt => println(f"Today's temperatures for ${lt._1}:  ${lt._2._1}%1.1f - ${lt._2._2}%1.1f"))

}
