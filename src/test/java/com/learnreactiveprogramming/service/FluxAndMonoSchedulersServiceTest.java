package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.test.StepVerifier;

class FluxAndMonoSchedulersServiceTest {

    private FluxAndMonoSchedulersService testSubject;

    @BeforeEach
    void setUp() {
        testSubject = new FluxAndMonoSchedulersService();
    }

    @Test
    void explore_publishOn() {
        //given

        //when
        final Flux<String> stringFlux = testSubject.explore_publishOn();

        //then
        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_subscribeOn() {
        //given

        //when
        final Flux<String> stringFlux = testSubject.explore_subscribeOn();

        //then
        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel() {
        //given

        //when
        final ParallelFlux<String> stringFlux = testSubject.explore_parallell();

        //then
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallell_using_flatmap() {
        //given

        //when
        final Flux<String> stringFlux = testSubject.explore_parallell_using_flatmap();

        //then
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallell_using_flatmap_1() {
        //given

        //when
        final Flux<String> stringFlux = testSubject.explore_parallell_using_flatmap_1();

        //then
        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallell_using_flatmapSequencial() {
        //given

        //when
        final Flux<String> stringFlux = testSubject.explore_parallell_using_flatmapSequencial();

        //then
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }
}