package service

// We import the models for Country, Airport, and Runway
import model.{Country, Airport, Runway}

object QueryService {

  // This class stores the final result: country name, airport name, and list of runway IDs
  case class QueryResult(country: String, airport: String, runways: List[String])
  // This class is used when there is an error (like bad input)
  case class QueryError(message: String)

  // This function checks if the input "kind of" matches the target (not exact match)
  private def fuzzyMatch(input: String, target: String): Boolean = {
    val inputLower = input.toLowerCase
    val targetLower = target.toLowerCase
    
    // First, try exact match or if input is inside target
    if (targetLower == inputLower || targetLower.contains(inputLower)) {
      true
    } else {
      // If not, check if all letters of input appear in order in target
      val inputChars = inputLower.toList
      
      @scala.annotation.tailrec
      def checkSequence(remaining: List[Char], targetIndex: Int): Boolean = {
        remaining match {
          case Nil => true // all letters found
          case c :: rest =>
            val nextIndex = targetLower.indexOf(c, targetIndex)
            if (nextIndex >= 0) 
              checkSequence(rest, nextIndex + 1)
            else
              false // letter not found
        }
      }

      // Try to match all letters in order
      checkSequence(inputChars, 0)
    }
  }

  // This function checks if the user's search is valid (not empty, not too short)
  def validateQuery(query: String): Either[QueryError, String] = {
    if (query.trim.isEmpty) Left(QueryError("La recherche ne peut pas être vide")) // empty
    else if (query.length < 2) Left(QueryError("La recherche doit contenir au moins 2 caractères")) // too short
    else Right(query.trim) // ok
  }

  // This function finds airports and runways based on the search
  def findAirportsAndRunways(query: String, countries: List[Country], airports: List[Airport], runways: List[Runway]): Either[QueryError, List[QueryResult]] = {
    validateQuery(query).map { validQuery =>
      // Find countries that match the search
      val matchingCountries = countries.filter(country =>
        country.name.toLowerCase.contains(validQuery.toLowerCase) ||
        country.code.toLowerCase.contains(validQuery.toLowerCase)
      )

      // If no country matched, return an error
      if (matchingCountries.isEmpty) {
        return Left(QueryError(s"Aucun pays trouvé pour la recherche: $validQuery"))
      }

      // Build results with airports and their runways
      val results = for {
        country <- matchingCountries
        airport <- airports.filter(_.isoCountry == country.code)
        airportRunways = runways.filter(_.airportRef == airport.id).map(_.le_ident)
        if airportRunways.nonEmpty
      } yield QueryResult(country.name, airport.name, airportRunways)

      Right(results)
    }.flatten // get the final list or the error
  }

  // Report 1: Countries with the most and least airports
  def topAndBottomCountriesByAirports(countries: List[Country], airports: List[Airport]): Either[QueryError, (List[(String, Int)], List[(String, Int)])] = {
    if (countries.isEmpty || airports.isEmpty) {
      Left(QueryError("Données manquantes pour générer le rapport")) // missing data
    } else {
      // Group airports by country and count them
      val airportsByCountry = airports
        .groupBy(_.isoCountry)
        .map { case (countryCode, airportList) =>
          val countryName = countries.find(_.code == countryCode).map(_.name).getOrElse(countryCode)
          (countryName, airportList.length)
        }
        .toList

      val top10 = airportsByCountry.sortBy(-_._2).take(10) // most airports
      val bottom10 = airportsByCountry.sortBy(_._2).take(10) // least airports
      Right((top10, bottom10))
    }
  }

  // Report 2: Surface types of runways for each country
  def runwaySurfacesByCountry(countries: List[Country], airports: List[Airport], runways: List[Runway]): Either[QueryError, Map[String, Set[String]]] = {
    if (countries.isEmpty || airports.isEmpty || runways.isEmpty) {
      Left(QueryError("Données manquantes pour générer le rapport"))
    } else {
      // Create maps to find country name from airport
      val airportCountryMap = airports.map(a => a.id -> a.isoCountry).toMap
      val countryNameMap = countries.map(c => c.code -> c.name).toMap
      
      // Group runways by country and collect their surface types
      val surfacesByCountry = runways
        .filter(r => airportCountryMap.contains(r.airportRef))
        .groupBy(r => countryNameMap.getOrElse(airportCountryMap(r.airportRef), "Unknown"))
        .view
        .mapValues(_.map(_.surface).toSet)
        .toMap

      Right(surfacesByCountry)
    }
  }

  // Report 3: Most common runway identifiers (le_ident)
  def topLeIdent(runways: List[Runway]): Either[QueryError, List[(String, Int)]] = {
    if (runways.isEmpty) {
      Left(QueryError("Données manquantes pour générer le rapport"))
    } else {
      // Group by le_ident and count them, then take the top 10
      val top10 = runways
        .groupBy(_.le_ident)
        .view
        .mapValues(_.length)
        .toList
        .sortBy(-_._2)
        .take(10)
      Right(top10)
    }
  }
}
