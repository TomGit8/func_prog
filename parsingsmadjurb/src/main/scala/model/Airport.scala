package model

// This is the Airport class. It stores all info about one airport.
case class Airport(
  id: String,                // unique ID
  ident: String,             // identifier code
  airportType: String,       // type (e.g. small_airport, large_airport)
  name: String,              // airport name
  latitude_deg: String,      // latitude in degrees
  longitude_deg: String,     // longitude in degrees
  elevation_ft: String,      // elevation in feet
  continent: String,         // continent code
  isoCountry: String,        // country code (ISO format)
  isoRegion: String,         // region code
  municipality: String,      // city or town
  scheduled_service: String, // yes/no if there is scheduled service
  gps_code: String,          // GPS code
  iata_code: String,         // IATA code (used by airlines)
  local_code: String,        // local code
  home_link: String,         // link to airport website
  wikipedia_link: String,    // link to Wikipedia page
  keywords: String           // extra keywords
)

object Airport {

  // This function reads a CSV line and turns it into an Airport object
  def fromCsv(line: String): Option[Airport] = {
    // split the line by commas (","), keep empty fields (with -1)
    line.split(",", -1).toList match {

      // if we have at least 18 fields, create an Airport object
      case id :: ident :: airportType :: name :: latitude_deg :: longitude_deg :: elevation_ft :: continent :: isoCountry ::
           isoRegion :: municipality :: scheduled_service :: gps_code :: iata_code :: local_code :: home_link ::
           wikipedia_link :: keywords :: _ =>

        // trim all values (remove extra spaces) and return the airport
        Some(Airport(
          id.trim, ident.trim, airportType.trim, name.trim,
          latitude_deg.trim, longitude_deg.trim, elevation_ft.trim,
          continent.trim, isoCountry.trim, isoRegion.trim, municipality.trim,
          scheduled_service.trim, gps_code.trim, iata_code.trim,
          local_code.trim, home_link.trim, wikipedia_link.trim, keywords.trim
        ))

      // if the line doesn't match the pattern (not enough fields), return None
      case _ => None
    }
  }
}
