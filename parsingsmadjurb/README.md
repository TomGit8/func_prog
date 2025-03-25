# Airport Data Parser

A Scala application that parses and analyzes airport, country, and runway data from CSV files.

## Requirements

- Java 8 or higher
- SBT (Scala Build Tool) 1.0 or higher

## Installation

1. Clone the repository or download the source code
2. Navigate to the project directory in your terminal

```bash
cd parsingsmadjurb
```

3. Run SBT to download dependencies:

```bash
sbt update
```

## Running the Application

To run the application, use the SBT `run` command:

```bash
sbt run
```

The application will:
1. Parse the CSV files (countries.csv, airports.csv, runways.csv) from resources
2. Start the console menu interface

## Features

- **Query mode**: Search airports and runways by country name or code
  - Supports fuzzy name matching (e.g., typing "zimb" will match "Zimbabwe")
  
- **Reports mode**: Generate various statistics including:
  - Top 10 countries with the most airports and bottom 10 with the least
  - Types of runway surfaces by country
  - Most common runway identifiers (le_ident)

## Project Structure

- `src/main/scala/model/`: Data models for Country, Airport, and Runway
- `src/main/scala/parser/`: CSV parsing functionality
- `src/main/scala/service/`: Business logic for queries and reports
- `src/main/scala/ui/`: Console user interface
- `src/main/resources/`: CSV data files

## CSV Data Format

The application expects three CSV files in the resources folder:
- `countries.csv`: Country data with id, code, name, etc.
- `airports.csv`: Airport data with references to countries
- `runways.csv`: Runway data with references to airports

## Development

To compile the project without running:

```bash
sbt compile
```

To package as a JAR file:

```bash
sbt package
``` 