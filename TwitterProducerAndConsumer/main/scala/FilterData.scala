
import scala.util.control.Breaks._
import CountryList.{FullCountryName, CountryName2 , CountryName3 , IndiaCities, IndiaStates, USACities, SpainCities, ItalyCities}

object FilterData {

  /**
   * Brings the data into the required format and filter it
   * Changes the data format from "Apr 14 2020" to "2020-04-14"
   * Maps the cities to the corresponding countries
   * When then cities and countries are not present in the countryList map/sets then the data will not be stored in the database
   * @param value Concatenation string of date location and tweet
   * @return
   */
  def filter(value: String): (String,String,String) = {

    val mapper = Map("Jan" -> "01", "Feb" -> "02", "Mar" -> "03", "Apr" -> "04", "May" -> "05", "Jun" -> "06", "Jul" -> "07", "Aug" -> "08", "Sep" -> "09", "Oct" -> "10", "Nov" -> "11", "Dec" -> "12")

    val splitString = value.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")


    if (splitString.length == 3) {

      val fullDate = splitString(0)
      val splitDate = fullDate.split(", ")
      val month = mapper(splitDate(0).substring(1, 4))
      val date = splitDate(0).substring(5, splitDate(0).length)
      var year = 0
      if (splitDate.length == 2) {
        year = splitDate(1).substring(0, 4).toInt
      }
      else {
        year = 2020
      }
      val Date = year + "-" + month + "-" + date

      val fullTweet = splitString(1)
      val tweet = fullTweet.substring(1, fullTweet.length - 1)


      val loc = splitString(2).split(", ")
      var location = loc(loc.length - 1)

      if (location.length == 1)
        location = loc(0)
      var locationLength = location.length

      if (locationLength != 1) {
        if (location(0) == '"') {
          location = location.substring(1, locationLength)
          locationLength -= 1
        }
        if (location(locationLength - 1) == '"') {
          location = location.substring(0, locationLength - 1)
        }
      }

      var found = 0


      if (location.length == 2) {
        val newLocation = location.toUpperCase()
        val FoundCountry = CountryName2.get(newLocation)
        if (FoundCountry.isDefined) {
          found = 1
          location = FoundCountry.get
        }
      }

      if (found == 0 && location.length == 3) {
        val newLocation = location.toUpperCase()
        val FoundCountry = CountryName3.get(newLocation)
        if (FoundCountry.isDefined) {
          found = 1
          location = FoundCountry.get
        }
      }
      if (found == 0) {
        var firstCharacter = location(0).toLower
        val asciiOfCharacter = firstCharacter.toInt
        val firstCharacter1 = firstCharacter.toString
        if (asciiOfCharacter >= 97 && asciiOfCharacter <= 122 && firstCharacter1 != "x") {
          val listOfCountry = FullCountryName(firstCharacter1)
          if (listOfCountry.contains(location.capitalize)) {
            found = 1
            location = location.capitalize
          }
        }
      }

      breakable {
        if (found == 0) {
          while (true) {
            if (IndiaCities.contains(location.capitalize)) {
              found = 1
              location = "India"
              break()
            }
            if (IndiaStates.contains(location.capitalize)) {
              found = 1
              location = "India"
              break()
            }
            if (USACities.contains(location.capitalize)) {
              found = 1
              location = "United States of America"
              break()
            }
            if (SpainCities.contains(location.capitalize)) {
              found = 1
              location = "Spain"
              break()
            }
            if (ItalyCities.contains(location.capitalize)) {
              found = 1
              location = "Italy"
              break()
            }
            break()
          }
        }
      }
      if (found == 1) {
        return (Date,location,tweet)
      }
    }
    ("-1","-1","-1")
  }
}
