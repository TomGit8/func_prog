package service

import model.{Country, Airport, Runway}

object QueryService {
  case class QueryResult(country: String, airport: String, runways: List[String])
  case class QueryError(message: String)

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

  def validateQuery(query: String): Either[QueryError, String] = {
    if (query.trim.isEmpty) Left(QueryError("La recherche ne peut pas être vide"))
    else if (query.length < 2) Left(QueryError("La recherche doit contenir au moins 2 caractères"))
    else Right(query.trim)
  }

  def findAirportsAndRunways(query: String, countries: List[Country], airports: List[Airport], runways: List[Runway]): Either[QueryError, List[QueryResult]] = {
    validateQuery(query).map { validQuery =>
      val matchingCountries = countries.filter(country =>
        country.name.toLowerCase.contains(validQuery.toLowerCase) ||
        country.code.toLowerCase.contains(validQuery.toLowerCase)
      )

      if (matchingCountries.isEmpty) {
        return Left(QueryError(s"Aucun pays trouvé pour la recherche: $validQuery"))
      }

      val results = for {
        country <- matchingCountries
        airport <- airports.filter(_.isoCountry == country.code)
        airportRunways = runways.filter(_.airportRef == airport.id).map(_.le_ident)
        if airportRunways.nonEmpty
      } yield QueryResult(country.name, airport.name, airportRunways)

      Right(results)
    }.flatten
  }

  // report 1: countries with most and least airports
  def topAndBottomCountriesByAirports(countries: List[Country], airports: List[Airport]): Either[QueryError, (List[(String, Int)], List[(String, Int)])] = {
    if (countries.isEmpty || airports.isEmpty) {
      Left(QueryError("Données manquantes pour générer le rapport"))
    } else {
      val airportsByCountry = airports
        .groupBy(_.isoCountry)
        .map { case (countryCode, airportList) =>
          val countryName = countries.find(_.code == countryCode).map(_.name).getOrElse(countryCode)
          (countryName, airportList.length)
        }
        .toList

      val top10 = airportsByCountry.sortBy(-_._2).take(10)
      val bottom10 = airportsByCountry.sortBy(_._2).take(10)
      Right((top10, bottom10))
    }
  }

  // report 2: runway surface types by country
  def runwaySurfacesByCountry(countries: List[Country], airports: List[Airport], runways: List[Runway]): Either[QueryError, Map[String, Set[String]]] = {
    if (countries.isEmpty || airports.isEmpty || runways.isEmpty) {
      Left(QueryError("Données manquantes pour générer le rapport"))
    } else {
      val airportCountryMap = airports.map(a => a.id -> a.isoCountry).toMap
      val countryNameMap = countries.map(c => c.code -> c.name).toMap
      
      val surfacesByCountry = runways
        .filter(r => airportCountryMap.contains(r.airportRef))
        .groupBy(r => countryNameMap.getOrElse(airportCountryMap(r.airportRef), "Unknown"))
        .view
        .mapValues(_.map(_.surface).toSet)
        .toMap

      Right(surfacesByCountry)
    }
  }

  // report 3: most common runway identifiers
  def topLeIdent(runways: List[Runway]): Either[QueryError, List[(String, Int)]] = {
    if (runways.isEmpty) {
      Left(QueryError("Données manquantes pour générer le rapport"))
    } else {
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
