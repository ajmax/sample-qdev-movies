# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Search**: 🏴‍☠️ Search the movie treasure chest with pirate-themed interface! Filter by movie name, ID, or genre
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Ahoy matey! Enjoy the pirate-themed search experience with authentic nautical language

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Search**: http://localhost:8080/movies/search (with query parameters)
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java    # Main Spring Boot application
│   │       ├── MoviesController.java     # REST controller for movie endpoints
│   │       ├── Movie.java                # Movie data model
│   │       ├── Review.java               # Review data model
│   │       └── utils/
│   │           ├── HTMLBuilder.java      # HTML generation utilities
│   │           └── MovieUtils.java       # Movie validation utilities
│   └── resources/
│       ├── application.yml               # Application configuration
│       ├── mock-reviews.json             # Mock review data
│       └── log4j2.xml                    # Logging configuration
└── test/                                 # Unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information, including a pirate-themed search form.

### Search Movies (🏴‍☠️ New Pirate Feature!)
```
GET /movies/search
```
Searches the movie treasure chest with pirate-themed responses! Supports filtering by multiple criteria.

**Query Parameters:**
- `name` (optional): Movie name for partial matching (case-insensitive)
- `id` (optional): Movie ID for exact matching (positive integer)
- `genre` (optional): Genre for partial matching (case-insensitive)

**Examples:**
```
# Search by movie name (partial match, case-insensitive)
http://localhost:8080/movies/search?name=prison

# Search by exact movie ID
http://localhost:8080/movies/search?id=1

# Search by genre (partial match, case-insensitive)
http://localhost:8080/movies/search?genre=drama

# Combine multiple search criteria (AND logic)
http://localhost:8080/movies/search?name=family&genre=crime

# Empty search returns all movies
http://localhost:8080/movies/search
```

**Pirate Language Features:**
- Search form with authentic pirate labels and placeholders
- Pirate-themed success and error messages
- Nautical terminology throughout the search experience
- "Arrr!", "Ahoy!", "Shiver me timbers!" and more pirate expressions

**Error Handling:**
- Invalid movie IDs (negative or zero): "Arrr! That movie ID be as worthless as fool's gold!"
- Movie names over 100 characters: "Blimey! That movie name be longer than a kraken's tentacle!"
- Genres over 50 characters: "Batten down the hatches! That genre be too long!"
- No search results: "Shiver me timbers! No movies found matching yer search criteria, matey!"

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate theming
- Add new features like advanced search filters or sorting
- Improve the responsive design
- Extend the pirate language vocabulary
- Add more search criteria (director, year, rating, etc.)

### Recent Additions
- **🏴‍☠️ Pirate-themed Movie Search**: Complete search functionality with authentic pirate language
- **Advanced Filtering**: Search by movie name, ID, and genre with case-insensitive partial matching
- **Comprehensive Validation**: Robust parameter validation with pirate-themed error messages
- **Responsive Search Form**: Mobile-friendly search interface with pirate styling
- **Full Test Coverage**: Extensive unit tests for all search functionality

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
