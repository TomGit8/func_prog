import parser.CsvParser
import model.{Country, Airport, Runway}
import ui.ConsoleUI

object Main {
  def main(args: Array[String]): Unit = {
    // Lecture des CSV
    val countriesLines = CsvParser.readLines("countries.csv")
    val airportsLines  = CsvParser.readLines("airports.csv")
    val runwaysLines   = CsvParser.readLines("runways.csv")

    val countries = countriesLines.flatMap(Country.fromCsv)
    val airports  = airportsLines.flatMap(Airport.fromCsv)
    val runways   = runwaysLines.flatMap(Runway.fromCsv)

    // Lancement de l'UI (menu)
    ConsoleUI.run(countries, airports, runways)
  }
}
