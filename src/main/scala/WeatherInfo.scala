/**
 * WeatherInfo describes an interface to obtain weather information
 * WeatherData describes the data associated with a weather measurement
 */

package org.bom.weather

import java.time.OffsetDateTime

// FALTA: Ver se realmente precisa dessa trait
trait WeatherInfo {
  def getCurrWeather: WeatherData
}

case class WeatherData(location: Location,
                       localTime: OffsetDateTime,
                       condition: String,
                       temperature: Double,
                       pressure: Double,
                       humidity: Int) //extends WeatherInfo {
{
  override def toString: String =
    s"${location.code}|${location.latitude},${location.longitude}|" +
      s"${localTime}|${condition}|${temperature}|${pressure}|${humidity}"
}
