/**
 * Season describes the annual seasons
 */

package org.bom.weather.seasons

import java.time.{OffsetDateTime, MonthDay}

sealed trait Season {
  object Hemisphere extends Enumeration {
    val NORTH, SOUTH = Value
    def forLat(latitude: Double) = if (latitude >= 0.0) NORTH else SOUTH
  }
  val start: Map[Hemisphere.Value, MonthDay]
  val end: Map[Hemisphere.Value, MonthDay]
  val daytimeOffsetFactor: Double
  val seasonTempFactor: Double

  def isCurrent(latitude: Double, dateTime: OffsetDateTime): Boolean = {
    // Jan/Feb dates are after northern WINTER / southern SUMMER start last year
    // Dec dates are before northern WINTER / southern SUMMER end next year
    val northLat = Hemisphere.forLat(latitude) == Hemisphere.NORTH
    val northWsouthS = (northLat && this == WINTER) || (!northLat && this == SUMMER)
    val dec = if ((dateTime.getMonthValue < 3) && northWsouthS) 1 else 0
    val inc = if ((dateTime.getMonthValue == 12) && northWsouthS) 1 else 0
    val startDT = start(Hemisphere.forLat(latitude)).atYear(dateTime.getYear - dec)
    val endDT = end(Hemisphere.forLat(latitude)).atYear(dateTime.getYear + inc)
    // Check season range for informed datetime
    startDT.isBefore(dateTime.toLocalDate) && endDT.isAfter(dateTime.toLocalDate)
  }
}

object Season {
  val all = List(SPRING, SUMMER, AUTUMN, WINTER)
  def apply(latitude: Double, dateTime: OffsetDateTime): Season =
    all.filter(_.isCurrent(latitude, dateTime)).head
}

case object SPRING extends Season {
  val start = Map(Hemisphere.NORTH -> MonthDay.of(2,29),
                  Hemisphere.SOUTH -> MonthDay.of(8,31))
  val end =   Map(Hemisphere.NORTH -> MonthDay.of(6,1),
                  Hemisphere.SOUTH -> MonthDay.of(12,1))
  val daytimeOffsetFactor = -1.0
  val seasonTempFactor = 1.2
}

case object SUMMER extends Season {
  val start = Map(Hemisphere.NORTH -> MonthDay.of(5,31),
                  Hemisphere.SOUTH -> MonthDay.of(11,30))
  val end =   Map(Hemisphere.NORTH -> MonthDay.of(9,1),
                  Hemisphere.SOUTH -> MonthDay.of(3,1))
  val daytimeOffsetFactor = -2.0
  val seasonTempFactor = 1.45
}

case object AUTUMN extends Season {
  val start = Map(Hemisphere.NORTH -> MonthDay.of(8,31),
                  Hemisphere.SOUTH -> MonthDay.of(2,29))
  val end =   Map(Hemisphere.NORTH -> MonthDay.of(12,1),
                  Hemisphere.SOUTH -> MonthDay.of(6,1))
  val daytimeOffsetFactor = 1.0
  val seasonTempFactor = 0.8
}

case object WINTER extends Season {
  val start = Map(Hemisphere.NORTH -> MonthDay.of(11,30),
                  Hemisphere.SOUTH -> MonthDay.of(5,31))
  val end =   Map(Hemisphere.NORTH -> MonthDay.of(3,1),
                  Hemisphere.SOUTH -> MonthDay.of(9,1))
  val daytimeOffsetFactor = 2.0
  val seasonTempFactor = 0.65
}
