/**
 * Location describes the climate of a specific location (undifferentiated)
 *
 * Parameters:
 *  - code: 3 letter location's identifier- example: "SYD"
 *  - zoneId: timezone identifier - example: "Australia/Sydney"
 *
 * Superclass parameters:
 *  - baseTemp: base temperature at the equator (latitude 0)
 *  - latitude: degrees with decimal notation
 *  - longitude: degrees with decimal notation
 *  - altitude: in meters from sea level
 */

package org.bom.weather

class Location(val code: String,
               val zoneId: String,
                   baseTemp: Double,
               val latitude: Double,
               val longitude: Double,
               val altitude: Int)
  extends Climate(baseTemp, latitude, longitude, altitude)
