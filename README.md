# Airport Project

For submission you should give back your last commit hash hear, and a googledrive link to a video showing your project working at the given commit hash.
The git repo and video should be private and shared with me : issamfalih@gmail.com
You must start your project in command line using : git log, sbt run. Or launch from on IDE where your commit hash is visible.

## MAIN PART (for every one to reach 10)
1.  For this project you'll write a program parsing 3 CSV files. The files contain data for countries, airports and runway information. Files must remain intact.
  You will create an sbt scala project. Feel free to use any scala library/framework as to write your test. No dependency is allowed to parse CSV.
  You must not use «require» loops «try», «catch», «var», «for», «return», «.get from Option type», «null», «throw», «head» keywords (unless you're writing a optional part, in that case it may be allowed if you ask first).
  You must not use a foreach in another foreach.
  If you don't want to use a database, you're allowed to use mutable(s) collections(s) instead. The sole purpose of the mutable(s) collection(s) must be to replaces a database. 

	Code instructions:
  Code for parsing, storing/quering and user interface should be in 3 different class, object or package.
  User interface code (for instance println) should only be in the user interface package/class.
  Runway and Airport must have there own case class and companion object. Each companion object must contain a method from to deserialise method from CSV line. This method must convert a CSV line to an instance of the case class.

2.1  You're program will be a command line program that will ask the user for two options - Query or Reports.

2.2 Query Option will ask the user for the country name or code and display the airports & runways at each airport. The input can be country code or country name.

2.3 Choosing Reports will display the following (possibly through a menu):
• 10 countries with highest number of airports (with count) and countries  with lowest number of airports.
• Type of runways (as indicated in "surface" column) per country
• The top 10 most common runway latitude (indicated in "le_ident" column)

### If you get the job done (10pt) with a clean code (4pt), correct submission (1pt) you'll have up to 15/20.


## CHOSEN PART
C.1) In 2.2 make the name matching partial/fuzzy. e.g. entering zimb will result in Zimbabwe (2pt)

C.2) Use database (in memory for the exercice like h2,sqlite) (4pt)

C.3) Do a GUI (6pt)

C.4) provide a rest API (4pt)

C.5) Use Future to improve the speed of your app (2pt)

C.6) Same as 0.5 with parallel collection (1pt)

You may theorically have more than 20/20, though the CTI rules will block it to 20/20

For chosen part 2 to 4 you're allowed to use scala libraries
O.2) Anorm, slick, squeryl, reactive-mongo, Casbah, elastic4s, Quil, doobie, Scalikejdbc, sdbc, sorm, mongo-scala-driver
O.4) Finch, http4s, Akka Http, Spray, Play (finatra and scalatra are forbidden)
