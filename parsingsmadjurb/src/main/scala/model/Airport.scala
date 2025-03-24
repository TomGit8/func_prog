package model

case class Airport(id: String, ident: String, airportType: String, name: String, latitude_deg: String,
                   longitude_deg: String, elevation_ft: String, continent: String, isoCountry: String,
                   isoRegion: String, municipality: String, scheduled_service: String, gps_code: String,
                   iata_code: String, local_code: String, home_link: String, wikipedia_link: String,
                   keywords: String)

object Airport {
  def fromCsv(line: String): Option[Airport] = {
    line.split(",", -1).toList match {
      case id :: ident :: airportType :: name :: latitude_deg :: longitude_deg :: elevation_ft :: continent :: isoCountry ::
           isoRegion :: municipality :: scheduled_service :: gps_code :: iata_code :: local_code :: home_link ::
           wikipedia_link :: keywords :: _ =>
        Some(Airport(id.trim, ident.trim, airportType.trim, name.trim, latitude_deg.trim, longitude_deg.trim,
          elevation_ft.trim, continent.trim, isoCountry.trim, isoRegion.trim, municipality.trim,
          scheduled_service.trim, gps_code.trim, iata_code.trim, local_code.trim, home_link.trim,
          wikipedia_link.trim, keywords.trim))
      case _ => None
    }
  }
}
