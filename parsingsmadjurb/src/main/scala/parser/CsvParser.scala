package parser

import scala.io.Source

object CsvParser {
  def readLines(resourceName: String): List[String] = {
    val source = Source.fromResource(resourceName)
    val lines = source.getLines().toList.drop(1) // on retire l'en-tête
    source.close()
    lines
  }
}
