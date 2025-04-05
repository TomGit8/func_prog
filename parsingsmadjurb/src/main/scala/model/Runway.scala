package model

// This class stores all information about a runway
case class Runway(
  id: String,                         // unique ID of the runway
  airportRef: String,                // ID of the airport this runway belongs to
  airport_ident: String,             // identifier code of the airport
  length_ft: String,                 // length of the runway in feet
  width_ft: String,                  // width of the runway in feet
  surface: String,                   // type of surface (asphalt, grass, etc.)
  lighted: String,                   // "1" if lighted, "0" if not
  closed: String,                    // "1" if closed, "0" if open
  le_ident: String,                  // identifier of the "LE" (low end) of the runway
  le_latitude_deg: String,           // latitude of the "LE" end
  le_longitude_deg: String,          // longitude of the "LE" end
  le_elevation_ft: String,          // elevation of the "LE" end
  le_heading_degT: String,          // heading of the "LE" end in degrees
  le_displaced_threshold_ft: String,// displaced threshold for "LE" in feet
  he_ident: String,                  // identifier of the "HE" (high end) of the runway
  he_latitude_deg: String,          // latitude of the "HE" end
  he_longitude_deg: String,         // longitude of the "HE" end
  he_elevation_ft: String,          // elevation of the "HE" end
  he_heading_degT: String,          // heading of the "HE" end in degrees
  he_displaced_threshold_ft: String // displaced threshold for "HE" in feet
)

object Runway {

  // This function reads a CSV line and tries to create a Runway object
  def fromCsv(line: String): Option[Runway] = {
    // split the line by commas (","), keeping empty fields
    line.split(",", -1).toList match {

      // if there are enough fields, create the Runway object
      case id :: airportRef :: airport_ident :: length_ft :: width_ft :: surface :: lighted :: closed :: le_ident :: le_latitude_deg ::
           le_longitude_deg :: le_elevation_ft :: le_heading_degT :: le_displaced_threshold_ft ::
           he_ident :: he_latitude_deg :: he_longitude_deg :: he_elevation_ft :: he_heading_degT :: he_displaced_threshold_ft :: _ =>

        // remove spaces and return the runway
        Some(Runway(
          id.trim, airportRef.trim, airport_ident.trim, length_ft.trim, width_ft.trim, surface.trim,
          lighted.trim, closed.trim, le_ident.trim, le_latitude_deg.trim, le_longitude_deg.trim,
          le_elevation_ft.trim, le_heading_degT.trim, le_displaced_threshold_ft.trim,
          he_ident.trim, he_latitude_deg.trim, he_longitude_deg.trim, he_elevation_ft.trim,
          he_heading_degT.trim, he_displaced_threshold_ft.trim
        ))

      // if the line doesn't have the right number of fields, return None
      case _ => None
    }
  }
}
