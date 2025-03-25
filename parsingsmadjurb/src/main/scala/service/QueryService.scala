package service

import model.{Country, Airport, Runway}

object QueryService {

  // fuzzy match helper - checks if input partially matches target string
  private def fuzzyMatch(input: String, target: String): Boolean = {
    val inputLower = input.toLowerCase
    val targetLower = target.toLowerCase
    
    // quick check first - exact or substring match
    if (targetLower == inputLower || targetLower.contains(inputLower)) {
      true
    } else {
      // try finding all characters in sequence
      val inputChars = inputLower.toList
      
      @scala.annotation.tailrec
      def checkSequence(remaining: List[Char], targetIndex: Int): Boolean = {
        remaining match {
          case Nil => true // found everything
          case c :: rest =>
            val nextIndex = targetLower.indexOf(c, targetIndex)
            if (nextIndex >= 0) 
              checkSequence(rest, nextIndex + 1)
            else
              false // couldn't find this char
        }
      }
      
      // see if we can find all chars in order
      checkSequence(inputChars, 0)
    }
  }

  // finds airports and runways for a country by name or code
  def findAirportsAndRunways(input: String,
                             countries: List[Country],
                             airports: List[Airport],
                             runways: List[Runway]): List[(String, String, List[String])] = {
    // find matching countries by code or fuzzy name match
    val matchedCountries = countries.filter { c =>
      c.code.equalsIgnoreCase(input) ||
      fuzzyMatch(input, c.name)
    }

    // group airports by country code
    val airportsByCountry = airports.groupBy(_.isoCountry)
    // group runways by airport id
    val runwaysByAirport = runways.groupBy(_.airportRef)

    // build result: (country name, airport name, runway identifiers)
    matchedCountries.flatMap { country =>
      val countryAirports = airportsByCountry.getOrElse(country.code, List())
      countryAirports.map { airport =>
        val airportRunways = runwaysByAirport.getOrElse(airport.id, List())
        val runwayIdentifiers = airportRunways.map(_.le_ident)
        (country.name, airport.name, runwayIdentifiers)
      }
    }
  }

  // report 1: countries with most and least airports
  def topAndBottomCountriesByAirports(countries: List[Country],
                                      airports: List[Airport]): (List[(String, Int)], List[(String, Int)]) = {
    // count airports per country
    val countByIso: Map[String, Int] = airports.groupBy(_.isoCountry).mapValues(_.size).toMap
    // map from country code to name
    val codeToName: Map[String, String] = countries.map(c => c.code -> c.name).toMap
    // convert to (country name, airport count) pairs
    val airportCounts: List[(String, Int)] = countByIso.toList.map { case (iso, count) =>
      (codeToName.getOrElse(iso, iso), count)
    }
    // sort by count
    val sorted = airportCounts.sortBy(_._2)
    val bottom10 = sorted.take(10)
    val top10 = sorted.reverse.take(10)
    (top10, bottom10)
  }

  // report 2: runway surface types by country
  def runwaySurfacesByCountry(countries: List[Country],
                              airports: List[Airport],
                              runways: List[Runway]): List[(String, Set[String])] = {
    val airportsByCountry = airports.groupBy(_.isoCountry)
    val runwaysByAirport = runways.groupBy(_.airportRef)
    val codeToName: Map[String, String] = countries.map(c => c.code -> c.name).toMap

    airportsByCountry.toList.map { case (iso, airportList) =>
      val countryName = codeToName.getOrElse(iso, iso)
      // get all runway surfaces for each airport
      val surfaces = airportList.flatMap(a => runwaysByAirport.getOrElse(a.id, List()).map(_.surface))
      (countryName, surfaces.toSet)
    }
  }

  // report 3: most common runway identifiers
  def topLeIdent(runways: List[Runway]): List[(String, Int)] = {
    val countByLeIdent: Map[String, Int] = runways.groupBy(_.le_ident).mapValues(_.size).toMap
    countByLeIdent.toList.sortBy(-_._2).take(10)
  }
}
