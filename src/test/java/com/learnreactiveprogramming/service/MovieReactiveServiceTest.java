package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

    private MovieReactiveService testSubject;
    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().build();
        movieInfoService = new MovieInfoService(webClient);
        reviewService = new ReviewService(webClient);
        revenueService = new RevenueService();
        testSubject = new MovieReactiveService(movieInfoService, reviewService, revenueService);
    }

    @Test
    void getAllMovies() {
        //given

        //then
        var movies = testSubject.getAllMovies();

        //then
        StepVerifier.create(movies)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .assertNext(movie -> {
                    assertEquals("The Dark Knight", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .assertNext(movie -> {
                    assertEquals("Dark Knight Rises", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        //given
        Long movieId = 100L;

        //then
        var movies = testSubject.getMovieById(movieId);

        //then

        StepVerifier.create(movies)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdFlatMap() {
        //given
        Long movieId = 100L;

        //then
        var movies = testSubject.getMovieByIdFlatMap(movieId);

        //then

        StepVerifier.create(movies)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieById_withRevenue() {
        //given
        Long movieId = 100L;

        //then
        var movies = testSubject.getMovieById_withRevenue(movieId);

        //then

        StepVerifier.create(movies)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }
}