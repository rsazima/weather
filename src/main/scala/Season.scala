/**
 * Season describes the annual seasons
 *
protected def seasonsDates =
    if (latitude >= 0) Map("Spring" -> MonthDay.of(3,1), "Summer" -> MonthDay.of(6,1),
                           "Autumn" -> MonthDay.of(9,1), "Winter" -> MonthDay.of(12,1))
    else Map("Autumn" -> MonthDay.of(3,1), "Winter" -> MonthDay.of(6,1),
             "Spring" -> MonthDay.of(9,1), "Summer" -> MonthDay.of(12,1))

 */

package org.bom.weather

import java.time.{OffsetDateTime, MonthDay}

// Symbolic literal representation of seasons
//  val (spring, summer, autumn, winter) = ('spring, 'summer, 'autumn, 'winter)

// Enumeration representation of seasons
//object Season extends Enumeration {
//  val SPRING, SUMMER, AUTUMN, WINTER = Value
//}

// Object with symbolic literal + utils
object Season {
  val (spring, summer, autumn, winter) = ('spring, 'summer, 'autumn, 'winter)
  val datesNorth =
    Map(spring -> MonthDay.of(3,1), summer -> MonthDay.of(6,1),
        autumn -> MonthDay.of(9,1), winter -> MonthDay.of(12,1))
  val datesSouth =
    Map(autumn -> MonthDay.of(3,1), winter -> MonthDay.of(6,1),
        spring -> MonthDay.of(9,1), summer -> MonthDay.of(12,1))
}
