package model

case class Country(id: String, code: String, name: String, continent: String, wikipedia_link: String, keywords: String)

object Country {
  def fromCsv(line: String): Option[Country] = {
    line.split(",", -1).toList match {
      case id :: code :: name :: continent :: wikipedia_link :: keywords :: _ =>
        Some(Country(id.trim, code.trim, name.trim, continent.trim, wikipedia_link.trim, keywords.trim))
      case _ => None
    }
  }
}
