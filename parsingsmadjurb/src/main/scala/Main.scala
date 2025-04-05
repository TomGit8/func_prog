import parser.CsvParser
import model.{Country, Airport, Runway}
import ui.ConsoleUI

object Main {
  def main(args: Array[String]): Unit = {
    // Read lines from the CSV files
    val countriesLines = CsvParser.readLines("countries.csv")
    val airportsLines  = CsvParser.readLines("airports.csv")
    val runwaysLines   = CsvParser.readLines("runways.csv")

    // Convert CSV lines into objects (Country, Airport, Runway)
    val countries = countriesLines.flatMap(Country.fromCsv)
    val airports  = airportsLines.flatMap(Airport.fromCsv)
    val runways   = runwaysLines.flatMap(Runway.fromCsv)

    // Start the user interface (menu in the console)
    ConsoleUI.run(countries, airports, runways)
  }
}
