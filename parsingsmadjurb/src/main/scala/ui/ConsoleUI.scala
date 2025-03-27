package ui

import model.{Country, Airport, Runway}
import service.QueryService
import service.QueryService.{QueryResult, QueryError}
import scala.annotation.tailrec

object ConsoleUI {

  def run(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    mainMenu(countries, airports, runways)
  }

  @tailrec
  def mainMenu(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    displayMainMenu()
    scala.io.StdIn.readLine().trim match {
      case "1" =>
        handleQuery(countries, airports, runways)
        mainMenu(countries, airports, runways)
      case "2" =>
        handleReports(countries, airports, runways)
        mainMenu(countries, airports, runways)
      case "3" =>
        println("Au revoir !")
      case _ =>
        println("Option invalide. Veuillez réessayer.")
        mainMenu(countries, airports, runways)
    }
  }

  private def displayMainMenu(): Unit = {
    println("\n=== Airport Runway Analyzer ===")
    println("1. Query")
    println("2. Reports")
    println("3. Exit")
    print("Choose an option (1-3): ")
  }

  private def handleQuery(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    println("\n=== Query Mode ===")
    println("Enter a country name or code (or 'back' to return to main menu):")
    val input = scala.io.StdIn.readLine().trim

    if (input.toLowerCase != "back") {
      val result = QueryService.findAirportsAndRunways(input, countries, airports, runways)
      result match {
        case Left(error) =>
          println(s"\nError: ${error.message}")
        
        case Right(queryResults) =>
          if (queryResults.isEmpty) {
            println(s"\nNo results found for '$input'")
          } else {
            println("\nResults:")
            queryResults.foreach { result =>
              println(s"\nCountry: ${result.country}")
              println(s"Airport: ${result.airport}")
              println(s"Runways: ${result.runways.mkString(", ")}")
            }
          }
      }
      println("\nPress Enter to continue...")
      scala.io.StdIn.readLine()
    }
  }

  private def handleReports(countries: List[Country], airports: List[Airport], runways: List[Runway]): Unit = {
    var showReportsMenu = true
    while (showReportsMenu) {
      println("\n=== Reports Menu ===")
      println("1. Top/Bottom 10 countries by airports count")
      println("2. Runway surface types by country")
      println("3. Top 10 most common runway identifiers")
      println("4. Back to main menu")
      print("Choose a report (1-4): ")

      scala.io.StdIn.readLine().trim match {
        case "1" =>
          val result = QueryService.topAndBottomCountriesByAirports(countries, airports)
          result match {
            case Left(error) =>
              println(s"\nError: ${error.message}")
            
            case Right((top10, bottom10)) =>
              println("\n=== Top 10 Countries by Number of Airports ===")
              top10.foreach { case (country, count) =>
                println(f"$country: $count airports")
              }
              
              println("\n=== Bottom 10 Countries by Number of Airports ===")
              bottom10.foreach { case (country, count) =>
                println(f"$country: $count airports")
              }
          }
          println("\nPress Enter to continue...")
          scala.io.StdIn.readLine()

        case "2" =>
          val result = QueryService.runwaySurfacesByCountry(countries, airports, runways)
          result match {
            case Left(error) =>
              println(s"\nError: ${error.message}")
            
            case Right(surfaces) =>
              println("\n=== Runway Surface Types by Country ===")
              surfaces.foreach { case (country, surfaceTypes) =>
                println(s"\n$country:")
                surfaceTypes.toSeq.sorted.foreach { surface =>
                  println(s"  - $surface")
                }
              }
          }
          println("\nPress Enter to continue...")
          scala.io.StdIn.readLine()

        case "3" =>
          val result = QueryService.topLeIdent(runways)
          result match {
            case Left(error) =>
              println(s"\nError: ${error.message}")
            
            case Right(topRunwayIds) =>
              println("\n=== Top 10 Most Common Runway Identifiers ===")
              topRunwayIds.foreach { case (ident, count) =>
                println(f"$ident: $count occurrences")
              }
          }
          println("\nPress Enter to continue...")
          scala.io.StdIn.readLine()

        case "4" =>
          showReportsMenu = false

        case _ =>
          println("\nInvalid option. Please try again.")
      }
    }
  }
}
