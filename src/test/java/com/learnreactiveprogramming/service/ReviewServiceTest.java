package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewServiceTest {

    WebClient webClient;
    ReviewService reviewService;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();
        reviewService = new ReviewService(webClient);
    }

    @Test
    void retrieveReviewsFlux_RestClient() {
        //given
        Long movieInfoId = 1L;

        //when
        var reviewFlux = reviewService.retrieveReviewsFlux_RestClient(movieInfoId);

        //then
        StepVerifier
                .create(reviewFlux)
                .assertNext(review -> assertEquals("Nolan is the real superhero", review.getComment()))
                .verifyComplete();

    }
}