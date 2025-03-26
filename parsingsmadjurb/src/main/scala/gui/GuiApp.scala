package gui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextArea, ComboBox, TextField, TitledPane, ScrollPane}
import scalafx.scene.layout.{VBox, HBox, Priority}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import parser.CsvParser
import model.{Country, Airport, Runway}
import service.QueryService
import scalafx.scene.chart._
import scalafx.scene.layout.Region

object GuiApp extends JFXApp3 {

  // Charger les données CSV dès le démarrage (les CSV doivent être dans src/main/resources)
  val countries: List[Country] = CsvParser.readLines("countries.csv").flatMap(Country.fromCsv)
  val airports: List[Airport]   = CsvParser.readLines("airports.csv").flatMap(Airport.fromCsv)
  val runways: List[Runway]      = CsvParser.readLines("runways.csv").flatMap(Runway.fromCsv)

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Airport Runway Analyzer - GUI"
      scene = mainMenuScene
    }
  }

  // ---------------------------
  // Écran principal (Menu)
  // ---------------------------
  def mainMenuScene: Scene = new Scene(800, 600) {
    val titleLabel   = new Label("Airport Runway Analyzer") {
      style = "-fx-font-size: 24px; -fx-font-weight: bold;"
    }
    val infoLabel    = new Label("Choisissez une option:")
    val queryButton  = new Button("Query (Requête)")
    val reportsButton = new Button("Reports (Statistiques)")

    // Actions pour changer d'écran
    queryButton.onAction = _ => stage.scene = queryScene
    reportsButton.onAction = _ => stage.scene = reportsScene

    root = new VBox {
      spacing = 20
      padding = Insets(40)
      alignment = Pos.Center
      children = Seq(titleLabel, infoLabel, queryButton, reportsButton)
    }
  }

  // ---------------------------
  // Écran Query (Recherche)
  // ---------------------------
  def queryScene: Scene = new Scene(600, 500) {
    // Dropdown select section
    val dropdownLabel = new Label("Sélectionner un pays :")
    val countryNames: List[String] = countries.map(_.name).sorted.distinct
    val countryComboBox = new ComboBox[String] {
      items = ObservableBuffer(countryNames: _*)
      promptText = "Sélectionner un pays"
      prefWidth = 200
    }
    val dropdownSearchButton = new Button("Chercher")
    
    val dropdownBox = new HBox {
      spacing = 10
      children = Seq(dropdownLabel, countryComboBox, dropdownSearchButton)
      alignment = Pos.CenterLeft
    }
    
    // Search bar section
    val searchLabel = new Label("Rechercher un pays :")
    val searchField = new TextField {
      promptText = "Entrez un nom ou code de pays"
      prefWidth = 200
    }
    val searchButton = new Button("Chercher")
    
    val searchBox = new HBox {
      spacing = 10
      children = Seq(searchLabel, searchField, searchButton)
      alignment = Pos.CenterLeft
    }
    
    // Results area
    val resultArea = new TextArea {
      editable = false
      wrapText = true
      prefHeight = 300
      vgrow = Priority.Always
    }
    
    // Navigation
    val backButton = new Button("Retour au menu")
    
    // Group input methods in collapsible panes
    val dropdownPane = new TitledPane {
      text = "Recherche par liste déroulante"
      content = dropdownBox
      expanded = true
    }
    
    val searchPane = new TitledPane {
      text = "Recherche par texte (supporte la recherche partielle)"
      content = searchBox
      expanded = true
    }

    // Dropdown search action
    dropdownSearchButton.onAction = _ => {
      val selectedCountry = Option(countryComboBox.value.value)
      selectedCountry match {
        case Some(country) if country.nonEmpty =>
          performSearch(country)
        case _ =>
          resultArea.text = "Veuillez sélectionner un pays."
      }
    }
    
    // Text search action
    searchButton.onAction = _ => {
      val searchText = searchField.text.value
      if (searchText.isEmpty)
        resultArea.text = "Veuillez entrer un texte de recherche."
      else
        performSearch(searchText)
    }
    
    // Enter key in search field triggers search
    searchField.onAction = _ => searchButton.fire()
    
    // Common search function
    def performSearch(query: String): Unit = {
      val results = QueryService.findAirportsAndRunways(query, countries, airports, runways)
      val displayText = if (results.isEmpty)
        s"Aucun résultat pour '$query'."
      else {
        results.map { case (country, airport, runwayInfos) =>
          s"Pays : $country\nAéroport : $airport\nRunways (le_ident) : ${runwayInfos.mkString(", ")}\n"
        }.mkString("\n")
      }
      resultArea.text = displayText
    }

    // Retour au menu principal
    backButton.onAction = _ => stage.scene = mainMenuScene

    root = new VBox {
      spacing = 10
      padding = Insets(20)
      children = Seq(dropdownPane, searchPane, resultArea, backButton)
    }
  }

  // ---------------------------
  // Écran Reports (Statistiques)
  // ---------------------------
  def reportsScene: Scene = new Scene(800, 700) {
    val instructionLabel = new Label("Sélectionnez un rapport :") {
      style = "-fx-font-size: 18px; -fx-font-weight: bold;"
    }
    val report1Button = new Button("Top/Bottom 10 pays par aéroports")
    val report2Button = new Button("Types de surfaces par pays")
    val report3Button = new Button("Top 10 des runway (le_ident) fréquents")
    
    val chartContainer = new VBox {
      spacing = 10
      maxHeight = 400
      visible = false
    }
    
    val resultArea = new TextArea {
      editable = false
      wrapText = true
      prefHeight = 200
    }
    
    val backButton = new Button("Retour au menu")

    // Rapport 1 : Top et Bottom 10 pays selon le nombre d'aéroports
    report1Button.onAction = _ => {
      val (top10, bottom10) = QueryService.topAndBottomCountriesByAirports(countries, airports)
      
      // Create bar chart for top 10
      val xAxis = new CategoryAxis {
        label = "Pays"
      }
      val yAxis = new NumberAxis {
        label = "Nombre d'aéroports"
      }
      
      val barChart = new BarChart(xAxis, yAxis) {
        title = "Top 10 pays par nombre d'aéroports"
        categoryGap = 20
      }
      
      val series = new XYChart.Series[String, Number] {
        name = "Nombre d'aéroports"
        data = top10.map { case (country, count) =>
          XYChart.Data[String, Number](country, count)
        }
      }
      
      barChart.data() += series
      
      // Update UI
      chartContainer.children = Seq(barChart)
      chartContainer.visible = true
      
      val textTop = top10.map { case (country, count) => s"$country : $count aéroports" }.mkString("\n")
      val textBottom = bottom10.map { case (country, count) => s"$country : $count aéroports" }.mkString("\n")
      resultArea.text = s"=== TOP 10 ===\n$textTop\n\n=== BOTTOM 10 ===\n$textBottom"
    }

    // Rapport 2 : Types de surfaces par pays
    report2Button.onAction = _ => {
      val surfaces = QueryService.runwaySurfacesByCountry(countries, airports, runways)
      
      // Create pie chart for the most common surface types
      val surfaceCount = surfaces.flatMap(_._2).groupBy(identity).mapValues(_.size).toSeq.sortBy(-_._2).take(5)
      
      val pieChart = new PieChart {
        title = "Distribution des types de surfaces les plus communs"
        data = surfaceCount.map { case (surface, count) =>
          PieChart.Data(surface, count)
        }
      }
      
      // Update UI
      chartContainer.children = Seq(pieChart)
      chartContainer.visible = true
      
      val text = surfaces.map { case (country, surfaceSet) =>
        s"$country : ${surfaceSet.mkString(", ")}"
      }.mkString("\n")
      resultArea.text = text
    }

    // Rapport 3 : Top 10 des runway (le_ident) les plus fréquents
    report3Button.onAction = _ => {
      val topLeIdent = QueryService.topLeIdent(runways)
      
      // Create bar chart for top 10 le_ident
      val xAxis = new CategoryAxis {
        label = "Runway Identifier"
      }
      val yAxis = new NumberAxis {
        label = "Nombre d'occurrences"
      }
      
      val barChart = new BarChart(xAxis, yAxis) {
        title = "Top 10 des identifiants de piste les plus fréquents"
        categoryGap = 20
      }
      
      val series = new XYChart.Series[String, Number] {
        name = "Occurrences"
        data = topLeIdent.map { case (ident, count) =>
          XYChart.Data[String, Number](ident, count)
        }
      }
      
      barChart.data() += series
      
      // Update UI
      chartContainer.children = Seq(barChart)
      chartContainer.visible = true
      
      val text = topLeIdent.map { case (ident, count) =>
        s"$ident : $count occurrences"
      }.mkString("\n")
      resultArea.text = text
    }

    backButton.onAction = _ => stage.scene = mainMenuScene

    root = new VBox {
      spacing = 20
      padding = Insets(20)
      children = Seq(
        instructionLabel,
        new HBox {
          spacing = 10
          children = Seq(report1Button, report2Button, report3Button)
        },
        chartContainer,
        resultArea,
        backButton
      )
    }
  }
}
