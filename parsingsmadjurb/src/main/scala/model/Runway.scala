package model

case class Runway(id: String, airportRef: String, airport_ident: String, length_ft: String, width_ft: String,
                  surface: String, lighted: String, closed: String, le_ident: String, le_latitude_deg: String,
                  le_longitude_deg: String, le_elevation_ft: String, le_heading_degT: String, le_displaced_threshold_ft: String,
                  he_ident: String, he_latitude_deg: String, he_longitude_deg: String, he_elevation_ft: String,
                  he_heading_degT: String, he_displaced_threshold_ft: String)

object Runway {
  def fromCsv(line: String): Option[Runway] = {
    line.split(",", -1).toList match {
      case id :: airportRef :: airport_ident :: length_ft :: width_ft :: surface :: lighted :: closed :: le_ident :: le_latitude_deg ::
           le_longitude_deg :: le_elevation_ft :: le_heading_degT :: le_displaced_threshold_ft ::
           he_ident :: he_latitude_deg :: he_longitude_deg :: he_elevation_ft :: he_heading_degT :: he_displaced_threshold_ft :: _ =>
        Some(Runway(id.trim, airportRef.trim, airport_ident.trim, length_ft.trim, width_ft.trim, surface.trim,
          lighted.trim, closed.trim, le_ident.trim, le_latitude_deg.trim, le_longitude_deg.trim,
          le_elevation_ft.trim, le_heading_degT.trim, le_displaced_threshold_ft.trim,
          he_ident.trim, he_latitude_deg.trim, he_longitude_deg.trim, he_elevation_ft.trim,
          he_heading_degT.trim, he_displaced_threshold_ft.trim))
      case _ => None
    }
  }
}
