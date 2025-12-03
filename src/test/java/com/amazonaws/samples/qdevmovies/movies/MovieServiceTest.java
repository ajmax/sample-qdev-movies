package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertEquals(12, movies.size()); // Based on the movies.json file
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals("John Director", movie.get().getDirector());
        assertEquals(1994, movie.get().getYear());
        assertEquals("Drama", movie.get().getGenre());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNull() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNegative() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdZero() {
        Optional<Movie> movie = movieService.getMovieById(0L);
        assertFalse(movie.isPresent());
    }

    // Search functionality tests

    @Test
    public void testSearchMoviesByNameExact() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartial() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameMultipleResults() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertTrue(results.size() > 1); // Multiple movies contain "The"
        
        // Verify all results contain "The" in their names
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenreExact() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertFalse(results.isEmpty());
        
        // Verify all results contain "Drama" in their genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenrePartial() {
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertFalse(results.isEmpty());
        
        // Verify all results contain "Crime" in their genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("crime"));
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "DRAMA");
        assertFalse(results.isEmpty());
        
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        // Search for a specific movie using multiple criteria
        List<Movie> results = movieService.searchMovies("Family", 2L, "Crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertEquals(2L, results.get(0).getId());
        assertTrue(results.get(0).getGenre().toLowerCase().contains("crime"));
    }

    @Test
    public void testSearchMoviesMultipleCriteriaNoMatch() {
        // Search with conflicting criteria
        List<Movie> results = movieService.searchMovies("Prison", 2L, null);
        assertTrue(results.isEmpty()); // Movie with ID 2 is not "Prison"
    }

    @Test
    public void testSearchMoviesEmptyName() {
        List<Movie> results = movieService.searchMovies("", null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesNullName() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesWhitespaceName() {
        List<Movie> results = movieService.searchMovies("   ", null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesEmptyGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "");
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesWhitespaceGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "   ");
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesGenreNoResults() {
        List<Movie> results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertTrue(results.isEmpty());
    }

    // Validation tests

    @Test
    public void testValidateSearchParametersValid() {
        List<String> errors = movieService.validateSearchParameters("Valid Movie", 1L, "Drama");
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateSearchParametersNullValues() {
        List<String> errors = movieService.validateSearchParameters(null, null, null);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateSearchParametersEmptyValues() {
        List<String> errors = movieService.validateSearchParameters("", null, "");
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateSearchParametersInvalidId() {
        List<String> errors = movieService.validateSearchParameters(null, -1L, null);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Arrr! That movie ID be as worthless as fool's gold"));
    }

    @Test
    public void testValidateSearchParametersZeroId() {
        List<String> errors = movieService.validateSearchParameters(null, 0L, null);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("fool's gold"));
    }

    @Test
    public void testValidateSearchParametersLongMovieName() {
        String longName = "A".repeat(101);
        List<String> errors = movieService.validateSearchParameters(longName, null, null);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Blimey! That movie name be longer than a kraken's tentacle"));
    }

    @Test
    public void testValidateSearchParametersMaxLengthMovieName() {
        String maxName = "A".repeat(100);
        List<String> errors = movieService.validateSearchParameters(maxName, null, null);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateSearchParametersLongGenre() {
        String longGenre = "A".repeat(51);
        List<String> errors = movieService.validateSearchParameters(null, null, longGenre);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("Batten down the hatches! That genre be too long"));
    }

    @Test
    public void testValidateSearchParametersMaxLengthGenre() {
        String maxGenre = "A".repeat(50);
        List<String> errors = movieService.validateSearchParameters(null, null, maxGenre);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateSearchParametersMultipleErrors() {
        String longName = "A".repeat(101);
        String longGenre = "B".repeat(51);
        List<String> errors = movieService.validateSearchParameters(longName, -1L, longGenre);
        assertEquals(3, errors.size());
        
        // Check that all error messages are present
        String allErrors = String.join(" ", errors);
        assertTrue(allErrors.contains("fool's gold"));
        assertTrue(allErrors.contains("kraken's tentacle"));
        assertTrue(allErrors.contains("Batten down the hatches"));
    }

    @Test
    public void testValidateSearchParametersWhitespaceHandling() {
        // Test that whitespace-only strings are handled correctly
        List<String> errors = movieService.validateSearchParameters("   ", null, "   ");
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testSearchMoviesTrimsWhitespace() {
        // Test that search properly trims whitespace
        List<Movie> results1 = movieService.searchMovies("Prison", null, null);
        List<Movie> results2 = movieService.searchMovies("  Prison  ", null, null);
        
        assertEquals(results1.size(), results2.size());
        if (!results1.isEmpty()) {
            assertEquals(results1.get(0).getMovieName(), results2.get(0).getMovieName());
        }
    }
}