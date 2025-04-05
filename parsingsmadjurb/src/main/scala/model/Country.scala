package model

// This is the Country class. It stores info about one country.
case class Country(
  id: String,             // unique ID of the country
  code: String,           // country code (ISO)
  name: String,           // name of the country
  continent: String,      // continent code
  wikipedia_link: String, // link to Wikipedia
  keywords: String        // extra keywords
)

object Country {

  // This function reads a CSV line and tries to create a Country object
  def fromCsv(line: String): Option[Country] = {
    // split the line by commas (","), keep empty values
    line.split(",", -1).toList match {

      // if there are at least 6 fields, create a Country object
      case id :: code :: name :: continent :: wikipedia_link :: keywords :: _ =>
        Some(Country(
          id.trim, code.trim, name.trim,
          continent.trim, wikipedia_link.trim, keywords.trim
        ))

      // if the line doesn’t match (too short), return None
      case _ => None
    }
  }
}
