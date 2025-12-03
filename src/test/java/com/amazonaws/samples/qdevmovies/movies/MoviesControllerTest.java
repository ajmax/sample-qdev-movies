package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with test data
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "The Family Boss", "Michael Filmmaker", 1972, "Crime/Drama", "Test description", 175, 5.0),
                new Movie(3L, "The Masked Hero", "Chris Moviemaker", 2008, "Action/Crime", "Test description", 152, 4.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(m -> m.getId().equals(id)).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String movieName, Long movieId, String genre) {
                List<Movie> results = new ArrayList<>();
                for (Movie movie : testMovies) {
                    boolean matches = true;
                    
                    if (movieName != null && !movieName.trim().isEmpty()) {
                        matches = movie.getMovieName().toLowerCase().contains(movieName.toLowerCase());
                    }
                    
                    if (matches && movieId != null) {
                        matches = movie.getId().equals(movieId);
                    }
                    
                    if (matches && genre != null && !genre.trim().isEmpty()) {
                        matches = movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                return results;
            }
            
            @Override
            public List<String> validateSearchParameters(String movieName, Long movieId, String genre) {
                List<String> errors = new ArrayList<>();
                if (movieId != null && movieId <= 0) {
                    errors.add("Arrr! That movie ID be as worthless as fool's gold - must be a positive number, matey!");
                }
                if (movieName != null && movieName.trim().length() > 100) {
                    errors.add("Blimey! That movie name be longer than a kraken's tentacle - keep it under 100 characters, ye scallywag!");
                }
                if (genre != null && genre.trim().length() > 50) {
                    errors.add("Batten down the hatches! That genre be too long - keep it under 50 characters, me hearty!");
                }
                return errors;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.getAttribute("movie");
        assertNotNull(movie);
        assertEquals("The Prison Escape", movie.getMovieName());
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        assertEquals("Movie Not Found", title);
    }

    // New search functionality tests
    
    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Prison", null, null, model);
        
        assertEquals("movies", result);
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertEquals("Prison", model.getAttribute("searchName"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Arrr! Found 1 movie treasure"));
    }
    
    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        String result = moviesController.searchMovies("FAMILY", null, null, model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Family Boss", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        
        assertEquals("movies", result);
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertEquals(2L, model.getAttribute("searchId"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Family Boss", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Drama", model);
        
        assertEquals("movies", result);
        assertEquals("Drama", model.getAttribute("searchGenre"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // "Drama" and "Crime/Drama"
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Ahoy! Discovered 2 movie treasures"));
    }
    
    @Test
    public void testSearchMoviesMultipleCriteria() {
        String result = moviesController.searchMovies("Family", 2L, "Crime", model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Family Boss", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistentMovie", null, null, model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Shiver me timbers! No movies found"));
    }
    
    @Test
    public void testSearchMoviesEmptyParameters() {
        String result = moviesController.searchMovies("", null, "", model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies
    }
    
    @Test
    public void testSearchMoviesInvalidId() {
        String result = moviesController.searchMovies(null, -1L, null, model);
        
        assertEquals("movies", result);
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        
        String title = (String) model.getAttribute("title");
        assertEquals("Search Parameters Invalid", title);
        
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("Arrr! That movie ID be as worthless as fool's gold"));
    }
    
    @Test
    public void testSearchMoviesLongMovieName() {
        String longName = "A".repeat(101); // 101 characters
        String result = moviesController.searchMovies(longName, null, null, model);
        
        assertEquals("movies", result);
        
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("Blimey! That movie name be longer than a kraken's tentacle"));
    }
    
    @Test
    public void testSearchMoviesLongGenre() {
        String longGenre = "A".repeat(51); // 51 characters
        String result = moviesController.searchMovies(null, null, longGenre, model);
        
        assertEquals("movies", result);
        
        String message = (String) model.getAttribute("message");
        assertTrue(message.contains("Batten down the hatches! That genre be too long"));
    }
    
    @Test
    public void testSearchMoviesPartialGenreMatch() {
        String result = moviesController.searchMovies(null, null, "Action", model);
        
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Masked Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("The Prison Escape", movies.get(0).getMovieName());
    }
    
    @Test
    public void testMovieServiceSearchIntegration() {
        List<Movie> results = mockMovieService.searchMovies("the", null, null);
        assertEquals(3, results.size()); // All movies contain "the" in their names
        
        results = mockMovieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        
        results = mockMovieService.searchMovies(null, null, "Crime");
        assertEquals(2, results.size()); // "Crime/Drama" and "Action/Crime"
    }
    
    @Test
    public void testMovieServiceValidation() {
        List<String> errors = mockMovieService.validateSearchParameters(null, -5L, null);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("fool's gold"));
        
        errors = mockMovieService.validateSearchParameters("A".repeat(101), null, null);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("kraken's tentacle"));
        
        errors = mockMovieService.validateSearchParameters(null, null, "A".repeat(51));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Batten down the hatches"));
        
        errors = mockMovieService.validateSearchParameters("Valid", 1L, "Valid");
        assertTrue(errors.isEmpty());
    }
}