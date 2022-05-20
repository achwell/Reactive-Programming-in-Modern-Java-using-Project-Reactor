package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class MovieReactiveServiceRestClientTest {

    private MovieReactiveService testSubject;

    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();
        var movieInfoService = new MovieInfoService(webClient);
        var reviewService = new ReviewService(webClient);
        var revenueService = new RevenueService();
        testSubject = new MovieReactiveService(movieInfoService, reviewService, revenueService);
    }

    @Test
    void getAllMovies_restClient() {
        //given

        //when
        var movieFlux = testSubject.getAllMovies_restClient();

        //then
        StepVerifier.create(movieFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieById_restClient() {
        //given
        Long movieId = 1L;

        //when
        var movieMono = testSubject.getMovieById_restClient(movieId);

        //then
        StepVerifier.create(movieMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}