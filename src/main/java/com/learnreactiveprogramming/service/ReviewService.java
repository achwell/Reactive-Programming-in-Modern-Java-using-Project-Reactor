package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Review;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.List;

public class ReviewService {


    private final WebClient webClient;

    public ReviewService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviewsFlux_RestClient(Long movieInfo) {
        final String uriString = UriComponentsBuilder
                .fromUriString("/v1/reviews")
                .queryParam("movieInfoId", movieInfo)
                .buildAndExpand()
                .toUriString();
        return webClient
                .get()
                .uri(uriString)
                .retrieve()
                .bodyToFlux(Review.class)
                .log();
    }



    public  List<Review> retrieveReviews(Long movieInfoId){

        return List.of(new Review(1L, movieInfoId, "Awesome Movie", 8.9),
                new Review(2L, movieInfoId, "Excellent Movie", 9.0));
    }

    public Flux<Review> retrieveReviewsFlux(long movieInfoId){

        var reviewsList = List.of(new Review(1L,movieInfoId, "Awesome Movie", 8.9),
                new Review(2L, movieInfoId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }

}
