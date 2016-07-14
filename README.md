# Weather System #

The weather system is composed of:

- Climate: abstract class that describes an extremely simplified climate model
- Seasons: set of trait and objects that represent the annual seasons
- Simulateable: trait with basic simulation interface
- Location: describes the climate of a specific location (undifferentiated)
            and generates weather data in a simulation
- WeatherData: describes the weather data generated in a simulation

Locations are created informing sensible values for: IATA code, ZoneId, base 
mean temperature, mean temperature range, coordinates(lat, lng), altitude.
Afterwards, weather data can be simulated for the current  moment or a specific 
point in time. Weather data contains:

    . location
    . localTime
    . condition
    . temperature
    . pressure
    . humidity
    
But prints (string conversion) as:

    "IATA CODE|lat,lng|UTC datetime|condition|temperature|pressure|humidity"

Simulation considers a range of parameters to generate minimally plausible
weather data:

- Temperature varies with latitude, altitude, season and time of day
- Pressure varies with altitude
- Condition (sun/clouds/rain/etc) has very simple rules and a bit of randomness
- Humidity varies with the current condition
- Limited topography extending the base Location class (example: desert)

What is NOT considered:

- Prevailing winds, air masses, fronts, etc
- Continentality, most astronomical variation and other complex phenomena
- Probabilities
- Actual historical weather data

## Usage ##

Typical usage of these components is as follows:

    /** Current locations */
    val rec = new Location("REC", "America/Recife", 24.0, 7.5, -8.3, -34.54, 0)
    val lpb = new Location("LPB", "America/La_Paz", 15.0, 15.0, -16.3, -68.09, 3640)
    val vcp = new Location("VCP", "America/Sao_Paulo", 22.0, 12.0, -22.54, -47.03, 700)
    val syd = new Location("SYD", "Australia/Sydney", 20.0, 11.0, -33.51, 151.12, 19)

    val weatherNetwork = Map[String, Location](
      rec.code -> rec
    , lpb.code -> lpb
    , vcp.code -> vcp
    , syd.code -> syd
    )

    // Parallel weather simulation of 10 repetitions with an 1 HOUR step
    weatherNetwork.par.mapValues(_.simulate()(10)())
    
## Testing ##

Tests where implemented using ScalaTest in FlatSpec style.

Test fixtures where declared in Fixtures.scala in order to modularise
tests and make them more concise.

To run them, simply type `sbt test` in the CLI at the root of the project.
