/**
 * FlatSpec for Location
 */

package org.bom.weather

import org.scalatest._

class LocationSpec extends FlatSpec with Matchers with BeforeAndAfter
                                    with LocationFixtures
{
  /** Location */

  // Clear skies after each cycle?
  //after { loc.clearItems() }

  "Temperature for DeadSea/Jordan" should "be in the high 10s" in {
    dse.temperature should (be > (15.0) and be < (20.0))
  }

  "Pressure for DeadSea/Jordan" should "be higher than 1070.00 hPa" in {
    dse.pressure should be > 1070.0
  }

  "Temperature for Pontianak/Indonesia" should "be 26.5" in {
    ptn.temperature should be (26.5)
  }

  "Pressure for Pontianak/Indonesia" should "be 1013.25 hPa" in {
    ptn.pressure should be (1013.25)
  }

  "Temperature for Campinas/Brazil" should "be in the low 10s" in {
    cpq.temperature should (be > (10.0) and be < (15.0))
  }

  "Pressure for Campinas/Brazil" should "be around 917 hPa" in {
    cpq.pressure.round should be (917)
  }

}