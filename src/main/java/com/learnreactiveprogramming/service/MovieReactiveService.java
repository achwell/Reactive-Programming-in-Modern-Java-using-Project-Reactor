package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {
    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;
    private final RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono =
                            reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                                    .collectList();
                    return reviewsMono
                            .map(reviewList -> new Movie(movieInfo, reviewList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                });
    }

    public Flux<Movie> getAllMovies_restClient() {
        return movieInfoService.retrieveAllMovieInfo_RestClient()
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono =
                            reviewService
                                    .retrieveReviewsFlux(movieInfo.getMovieInfoId())
                                    .collectList();
                    return reviewsMono.map(reviewList -> new Movie(movieInfo, reviewList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                });
    }

    public Mono<Movie> getMovieById_restClient(Long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoById_RestClient(movieId);
        var reviewFlux = reviewService.retrieveReviewsFlux(movieId).collectList();
        return movieInfoMono.zipWith(reviewFlux, (Movie::new));
    }

    public Flux<Movie> getAllMovies_retry() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    ;
                    throw new MovieException(ex.getMessage());
                })
                //.retry();
                .retry(3);
    }

    public Flux<Movie> getAllMovies_retryWhen() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                        .collectList()
                        .map(reviewsList -> new Movie(movieInfo, reviewsList)))
                .onErrorMap(throwable -> {
                    log.error("Exception is: ", throwable);
                    if (throwable instanceof NetworkException) {
                        throw new MovieException(throwable.getMessage());
                    }
                    throw new ServiceException(throwable.getMessage());
                })
                .retryWhen(retrySpec())
                .log();
    }

    public Mono<Movie> getMovieById(Long movieId) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(movieId)
                .zipWith(reviewService.retrieveReviewsFlux(movieId).collectList(), Movie::new)
                .log();
    }

    public Mono<Movie> getMovieById_withRevenue(Long movieId) {
        var revenueMono = Mono.
                fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());
        return movieInfoService.retrieveMovieInfoMonoUsingId(movieId)
                .zipWith(reviewService
                        .retrieveReviewsFlux(movieId)
                        .collectList(), Movie::new)
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                })
                .log();
    }

    public Mono<Movie> getMovieByIdFlatMap(Long movieId) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(movieId)
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                        .collectList()
                        .map(reviewsList -> new Movie(movieInfo, reviewsList)))
                .log();
    }

    public Flux<Movie> getAllMovies_repeat() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                        .collectList()
                        .map(reviewsList -> new Movie(movieInfo, reviewsList)))
                .onErrorMap(throwable -> {
                    log.error("Exception is: ", throwable);
                    if (throwable instanceof NetworkException) {
                        throw new MovieException(throwable.getMessage());
                    }
                    throw new ServiceException(throwable.getMessage());
                })
                .retryWhen(retrySpec())
                .repeat()
                .log();
    }

    public Flux<Movie> getAllMovies_repeat_n(Long n) {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(movieInfo -> reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                        .collectList()
                        .map(reviewsList -> new Movie(movieInfo, reviewsList)))
                .onErrorMap(throwable -> {
                    log.error("Exception is: ", throwable);
                    if (throwable instanceof NetworkException) {
                        throw new MovieException(throwable.getMessage());
                    }
                    throw new ServiceException(throwable.getMessage());
                })
                .retryWhen(retrySpec())
                .repeat(n)
                .log();
    }

    public static Retry retrySpec() {

        //  Retry with a back of 500 ms everytime
        //  return Retry.backoff(3, Duration.ofMillis(500));

        return Retry.fixedDelay(3, Duration.ofMillis(1000))
                .filter((ex) -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));
    }
}
