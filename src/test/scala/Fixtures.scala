/**
 * Test fixtures
 */

package org.bom.weather

import java.time.OffsetDateTime

trait LocationFixtures {

  type WeatherSeries = Vector[WeatherData]

  /** Current locations */
  val dse = new Location("DSE", "Jordan/Amman", 26.5, 31.56, 35.47, -423)
  val ptn = new Location("PTN", "Indonesia/Jakarta", 26.5, 0.0, 109.2, 0)
  val cpq = new Location("CPQ", "America/Sao_Paulo", 26.5, -23, -47, 700)

  val weatherNetwork = Map[String, Location](
      dse.code -> dse
    , ptn.code -> ptn
    , cpq.code -> cpq
//    , .code ->
//    , .code ->
//    , .code ->
//    , .code ->
//    , .code ->
//    , .code ->
//    , .code ->
//    , .code ->
  )

  // Default simulation period
  val from = OffsetDateTime.now()
  val to   = from.plusHours(10)

}
