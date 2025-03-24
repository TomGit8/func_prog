package service

import model.{Country, Airport, Runway}

object QueryService {

  // M.2.2 : Recherche des aéroports et runways pour un pays (par nom ou code)
  def findAirportsAndRunways(input: String,
                             countries: List[Country],
                             airports: List[Airport],
                             runways: List[Runway]): List[(String, String, List[String])] = {
    // Filtrer les pays correspondant (par code ou par nom)
    val matchedCountries = countries.filter { c =>
      c.code.equalsIgnoreCase(input) ||
      c.name.toLowerCase.contains(input.toLowerCase)
    }

    // Regrouper les aéroports par isoCountry
    val airportsByCountry = airports.groupBy(_.isoCountry)
    // Regrouper les runways par airportRef
    val runwaysByAirport = runways.groupBy(_.airportRef)

    // Construire le résultat final : (nomPays, nomAéroport, liste des le_ident des runways)
    matchedCountries.flatMap { country =>
      val countryAirports = airportsByCountry.getOrElse(country.code, List())
      countryAirports.map { airport =>
        val airportRunways = runwaysByAirport.getOrElse(airport.id, List())
        val runwayInfos = airportRunways.map(_.le_ident)
        (country.name, airport.name, runwayInfos)
      }
    }
  }

  // Rapport 1 : Top 10 et Bottom 10 pays selon le nombre d’aéroports
  def topAndBottomCountriesByAirports(countries: List[Country],
                                      airports: List[Airport]): (List[(String, Int)], List[(String, Int)]) = {
    // Regrouper les aéroports par isoCountry et compter
    val countByIso: Map[String, Int] = airports.groupBy(_.isoCountry).mapValues(_.size).toMap
    // Convertir le code pays en nom
    val codeToName: Map[String, String] = countries.map(c => c.code -> c.name).toMap
    // Transformer (isoCountry, count) en (countryName, count)
    val list: List[(String, Int)] = countByIso.toList.map { case (iso, count) =>
      (codeToName.getOrElse(iso, iso), count)
    }
    // Trier par nombre d'aéroports
    val sorted = list.sortBy(_._2)
    val bottom10 = sorted.take(10)
    val top10    = sorted.reverse.take(10)
    (top10, bottom10)
  }

  // Rapport 2 : Type de surfaces (champ "surface") par pays
  def runwaySurfacesByCountry(countries: List[Country],
                              airports: List[Airport],
                              runways: List[Runway]): List[(String, Set[String])] = {
    val airportsByCountry = airports.groupBy(_.isoCountry)
    val runwaysByAirport  = runways.groupBy(_.airportRef)
    val codeToName: Map[String, String] = countries.map(c => c.code -> c.name).toMap

    airportsByCountry.toList.map { case (iso, airportList) =>
      val countryName = codeToName.getOrElse(iso, iso)
      // Pour chaque aéroport, récupérer les surfaces des runways
      val surfaces = airportList.flatMap(a => runwaysByAirport.getOrElse(a.id, List()).map(_.surface))
      (countryName, surfaces.toSet)
    }
  }

  // Rapport 3 : Top 10 des valeurs de "le_ident" les plus fréquentes
  def topLeIdent(runways: List[Runway]): List[(String, Int)] = {
    val countByLeIdent: Map[String, Int] = runways.groupBy(_.le_ident).mapValues(_.size).toMap
    countByLeIdent.toList.sortBy(-_._2).take(10)
  }
}
