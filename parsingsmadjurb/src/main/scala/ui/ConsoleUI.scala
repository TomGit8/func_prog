package ui

import scala.io.StdIn
import model.{Country, Airport, Runway}
import service.QueryService
import annotation.tailrec

object ConsoleUI {

  def run(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    mainMenu(countries, airports, runways)
  }

  @tailrec
  private def mainMenu(countries: List[Country],
                       airports: List[Airport],
                       runways: List[Runway]): Unit = {
    println("=== MENU PRINCIPAL ===")
    println("1 - Query (Requête par pays)")
    println("2 - Reports (Statistiques)")
    println("0 - Quitter")
    StdIn.readLine().trim match {
      case "1" =>
        queryMode(countries, airports, runways)
        mainMenu(countries, airports, runways)
      case "2" =>
        reportsMenu(countries, airports, runways)
        mainMenu(countries, airports, runways)
      case "0" =>
        println("Au revoir !")
      case _ =>
        println("Choix invalide.")
        mainMenu(countries, airports, runways)
    }
  }

  private def queryMode(countries: List[Country],
                        airports: List[Airport],
                        runways: List[Runway]): Unit = {
    println("=== QUERY MODE ===")
    println("Entrez un nom de pays ou un code pays :")
    val input = StdIn.readLine().trim
    val result = QueryService.findAirportsAndRunways(input, countries, airports, runways)
    if (result.isEmpty)
      println(s"Aucun résultat pour '$input'.")
    else
      result.foreach { case (countryName, airportName, runwayInfos) =>
        println(s"Pays : $countryName | Aéroport : $airportName | Runways (le_ident) : ${runwayInfos.mkString(", ")}")
      }
    println("Appuyez sur Entrée pour revenir au menu principal.")
    StdIn.readLine()
  }

  @tailrec
  private def reportsMenu(countries: List[Country],
                          airports: List[Airport],
                          runways: List[Runway]): Unit = {
    println("=== MENU REPORTS ===")
    println("1 - 10 pays avec le plus d aeroports et 10 avec le moins d aeroports")
    println("2 - Type de surfaces des pistes par pays")
    println("3 - Top 10 des runway (le_ident) les plus frequents")
    println("0 - Retour au menu principal")
    StdIn.readLine().trim match {
      case "1" =>
        val (top10, bottom10) = QueryService.topAndBottomCountriesByAirports(countries, airports)
        println("=== TOP 10 ===")
        top10.foreach { case (country, count) => println(s"$country : $count aéroports") }
        println("=== BOTTOM 10 ===")
        bottom10.foreach { case (country, count) => println(s"$country : $count aéroports") }
        println("Appuyez sur Entrée pour continuer.")
        StdIn.readLine()
        reportsMenu(countries, airports, runways)
      case "2" =>
        val surfaces = QueryService.runwaySurfacesByCountry(countries, airports, runways)
        surfaces.foreach { case (countryName, surfaceTypes) =>
          println(s"Pays: $countryName => Surfaces: ${surfaceTypes.mkString(", ")}")
        }
        println("Appuyez sur Entrée pour continuer.")
        StdIn.readLine()
        reportsMenu(countries, airports, runways)
      case "3" =>
        val topLeIdent = QueryService.topLeIdent(runways)
        println("Top 10 des runway (le_ident) :")
        topLeIdent.foreach { case (ident, count) =>
          println(s"$ident : $count occurrences")
        }
        println("Appuyez sur Entrée pour continuer.")
        StdIn.readLine()
        reportsMenu(countries, airports, runways)
      case "0" =>
        println("Retour au menu principal.")
      case _ =>
        println("Choix invalide.")
        reportsMenu(countries, airports, runways)
    }
  }
}
