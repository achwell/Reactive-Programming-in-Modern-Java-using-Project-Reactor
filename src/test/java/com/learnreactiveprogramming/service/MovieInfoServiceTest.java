package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient;
    MovieInfoService movieInfoService;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();
        movieInfoService = new MovieInfoService(webClient);
    }

    @Test
    void retrieveAllMovieInfo_RestClient() {
        //given

        //when
        var movieInfoFlux = movieInfoService.retrieveAllMovieInfo_RestClient();

        //then
        StepVerifier
                .create(movieInfoFlux)
                .expectNextCount(7)
                .verifyComplete();

    }

    @Test
    void retrieveMovieInfoById_RestClient() {
        //given
        Long movieInfoId = 1L;

        //when
        var movieInfoMono = movieInfoService.retrieveMovieInfoById_RestClient(movieInfoId);

        //then
        StepVerifier
                .create(movieInfoMono)
                .expectNextCount(1)
                .verifyComplete();

    }
}