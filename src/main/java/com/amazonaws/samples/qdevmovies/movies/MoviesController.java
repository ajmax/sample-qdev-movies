package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Handles movie search requests with pirate-themed responses.
     * Accepts query parameters for movie name, ID, and genre filtering.
     * 
     * @param movieName Optional movie name for partial matching (case-insensitive)
     * @param movieId Optional movie ID for exact matching
     * @param genre Optional genre for partial matching (case-insensitive)
     * @param model Spring MVC model for template rendering
     * @return Template name for rendering search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String movieName,
            @RequestParam(value = "id", required = false) Long movieId,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Received search request - Name: '{}', ID: {}, Genre: '{}'", movieName, movieId, genre);
        
        // Validate search parameters
        List<String> validationErrors = movieService.validateSearchParameters(movieName, movieId, genre);
        if (!validationErrors.isEmpty()) {
            logger.warn("Arrr! Search parameters failed validation: {}", validationErrors);
            model.addAttribute("title", "Search Parameters Invalid");
            model.addAttribute("message", String.join(" ", validationErrors));
            model.addAttribute("searchPerformed", true);
            model.addAttribute("movies", movieService.getAllMovies());
            return "movies";
        }
        
        // Perform the search
        List<Movie> searchResults = movieService.searchMovies(movieName, movieId, genre);
        
        // Add search context to model
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchName", movieName);
        model.addAttribute("searchId", movieId);
        model.addAttribute("searchGenre", genre);
        
        // Add pirate-themed messages based on results
        if (searchResults.isEmpty()) {
            logger.info("Blimey! No movies found matching the search criteria");
            model.addAttribute("searchMessage", "Shiver me timbers! No movies found matching yer search criteria, matey. Try adjusting yer search or browse all our treasures below!");
        } else {
            logger.info("Yo ho ho! Found {} movies matching the search", searchResults.size());
            String resultMessage = searchResults.size() == 1 ? 
                "Arrr! Found 1 movie treasure matching yer search, ye savvy sailor!" :
                String.format("Ahoy! Discovered %d movie treasures matching yer search criteria, me hearty!", searchResults.size());
            model.addAttribute("searchMessage", resultMessage);
        }
        
        return "movies";
    }
}