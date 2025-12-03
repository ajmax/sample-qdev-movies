package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on provided criteria with pirate-themed logging.
     * Supports filtering by movie name (case-insensitive partial match), 
     * movie ID (exact match), and genre (case-insensitive partial match).
     * 
     * @param movieName The movie name to search for (partial match, case-insensitive)
     * @param movieId The exact movie ID to search for
     * @param genre The genre to search for (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String movieName, Long movieId, String genre) {
        logger.info("Ahoy! Searching the treasure chest for movies with criteria - Name: '{}', ID: {}, Genre: '{}'", 
                   movieName, movieId, genre);
        
        List<Movie> searchResults = new ArrayList<>();
        
        // If all parameters are null or empty, return all movies (like a treasure map showing all islands)
        if (isEmptySearchCriteria(movieName, movieId, genre)) {
            logger.info("Arrr! No search criteria provided, returning all movies from the treasure chest");
            return new ArrayList<>(movies);
        }
        
        for (Movie movie : movies) {
            if (matchesSearchCriteria(movie, movieName, movieId, genre)) {
                searchResults.add(movie);
            }
        }
        
        logger.info("Shiver me timbers! Found {} movies matching the search criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Checks if all search criteria are empty or null
     */
    private boolean isEmptySearchCriteria(String movieName, Long movieId, String genre) {
        return (movieName == null || movieName.trim().isEmpty()) &&
               movieId == null &&
               (genre == null || genre.trim().isEmpty());
    }

    /**
     * Determines if a movie matches the provided search criteria
     */
    private boolean matchesSearchCriteria(Movie movie, String movieName, Long movieId, String genre) {
        // Check movie name (case-insensitive partial match)
        if (movieName != null && !movieName.trim().isEmpty()) {
            if (!movie.getMovieName().toLowerCase().contains(movieName.trim().toLowerCase())) {
                return false;
            }
        }
        
        // Check movie ID (exact match)
        if (movieId != null) {
            if (!movie.getId().equals(movieId)) {
                return false;
            }
        }
        
        // Check genre (case-insensitive partial match)
        if (genre != null && !genre.trim().isEmpty()) {
            if (!movie.getGenre().toLowerCase().contains(genre.trim().toLowerCase())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Validates search parameters and returns validation errors with pirate language
     * 
     * @param movieName The movie name parameter
     * @param movieId The movie ID parameter  
     * @param genre The genre parameter
     * @return List of validation error messages, empty if all parameters are valid
     */
    public List<String> validateSearchParameters(String movieName, Long movieId, String genre) {
        List<String> errors = new ArrayList<>();
        
        // Validate movie ID if provided
        if (movieId != null && movieId <= 0) {
            errors.add("Arrr! That movie ID be as worthless as fool's gold - must be a positive number, matey!");
        }
        
        // Validate movie name length if provided
        if (movieName != null && movieName.trim().length() > 100) {
            errors.add("Blimey! That movie name be longer than a kraken's tentacle - keep it under 100 characters, ye scallywag!");
        }
        
        // Validate genre length if provided
        if (genre != null && genre.trim().length() > 50) {
            errors.add("Batten down the hatches! That genre be too long - keep it under 50 characters, me hearty!");
        }
        
        return errors;
    }
}
