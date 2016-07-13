/**
 * Test fixtures
 */

package org.bom.weather

import org.bom.weather._
import java.time.OffsetDateTime

trait LocationFixtures {

  def date(str: String) = OffsetDateTime.parse(str)

  /** Preset dates */
  val july12KLN = date("2016-07-12T13:15:30+01:00")
  val july12DSE = date("2016-07-12T13:15:30+02:00")
  val july12PTN = date("2016-07-12T13:15:30+07:00")
  val july12REC = date("2016-07-12T13:15:30-03:00")
  val july12CPQ = date("2016-07-12T13:15:30-03:00")
  val july12SYD = date("2016-07-12T13:15:30+10:00")

  /** Current locations */
  // Pontianak has a base temp (daily mean) of 27C, but the moderating effects of
  // continentality and topography are not modelled, so 27C would generate extremes
  // OR we can use an artificially narrower avgTempDelta. Opposite for Dead Sea.
  // FALTA: Checar cod IATA abaixo
  val kln = new Location("KLN", "Europe/Berlin", 15.0, 9.0, 50.56, 6.57, 58)
  val dse = new Location("DSE", "Asia/Amman", 26.0, 12.0, 31.56, 35.47, -423)
  val ptn = new Location("PTN", "Asia/Pontianak", 24.0, 7.0, 0.0, 109.2, 0)
  val rec = new Location("REC", "America/Recife", 24.0, 7.5, -8.3, -34.54, 0)
  val cpq = new Location("CPQ", "America/Sao_Paulo", 22.0, 12.0, -22.54, -47.03, 700)
  val syd = new Location("SYD", "Australia/Sydney", 20.0, 11.0, -33.51, 151.12, 0)

  val weatherNetwork = Map[String, Location](
      kln.code -> kln
    , dse.code -> dse
    , ptn.code -> ptn
    , rec.code -> rec
    , cpq.code -> cpq
    , syd.code -> syd
//    , .code ->
//    , .code ->
//    , .code ->
//    , .code ->
  )

  // Default simulation period
  val from = OffsetDateTime.now()
  val to   = from.plusHours(10)

  // Default simulation period
  val fromExt = OffsetDateTime.now()
  val toExt   = from.plusDays(180)

}
