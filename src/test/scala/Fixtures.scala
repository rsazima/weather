/**
 * Test fixtures
 */

package org.bom.weather

import java.time.OffsetDateTime

trait LocationFixtures {

  def date(str: String) = OffsetDateTime.parse(str)

  /** Preset dates */
  val july12MOW = date("2016-07-12T13:15:30+03:00")
  val july12CGN = date("2016-07-12T13:15:30+01:00")
  val july12AMM = date("2016-07-12T13:15:30+02:00")
  val july12MIA = date("2016-07-12T13:15:30-05:00")
  val july12PNK = date("2016-07-12T13:15:30+07:00")
  val july12REC = date("2016-07-12T13:15:30-03:00")
  val july12LPB = date("2016-07-12T13:15:30-04:00")
  val july12VCP = date("2016-07-12T13:15:30-03:00")
  val july12SYD = date("2016-07-12T13:15:30+10:00")
  val july12CHC = date("2016-07-12T13:15:30+12:00")

  /** Current locations */
  // Pontianak has a base temp (daily mean) of 27C, but the moderating effects of
  // continentality and topography are not modelled, so 27C would generate extremes
  // OR we can use an artificially narrower avgTempDelta. Opposite for Dead Sea.
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

  val weatherNetwork = Map[String, Location](
      mow.code -> mow
    , cgn.code -> cgn
    , amm.code -> amm
    , mia.code -> mia
    , pnk.code -> pnk
    , rec.code -> rec
    , lpb.code -> lpb
    , vcp.code -> vcp
    , syd.code -> syd
    , chc.code -> chc
  )

  // Default simulation period
  val from = OffsetDateTime.now()
  val to   = from.plusHours(10)

}
