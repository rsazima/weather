/**
 * Simulated describes an interface for simulated data.
 * It's possible to simulate for either a specified period or set
 * repetitions, both in even steps defined as a chronological unit.
 * One simulation cycle should be performed every step.
 */

package org.bom.weather

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

trait Simulated {
  def simulate(from: OffsetDateTime)
              (repetitions: Int)
              (step: ChronoUnit): Vector[WeatherData]
  def simulatePeriod(from: OffsetDateTime)
                    (to: OffsetDateTime)
                    (step: ChronoUnit): Vector[WeatherData] = {
    simulate(from)(step.between(from, to).toInt)(step)
  }
}
