package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.tools.agent.ReactorDebugAgent;

import java.time.Duration;
import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    private FluxAndMonoGeneratorService testSubject;

    @BeforeEach
    void setUp() {
        testSubject = new FluxAndMonoGeneratorService();
    }

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = testSubject.namesFlux();

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("alex")
                .expectNextCount(2)
                //.expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesMono() {
        var namesMono = testSubject.namesMono();
        StepVerifier
                .create(namesMono)
                .expectNext("alex")
                .verifyComplete();
    }


    @Test
    void namesFluxMap() {
        //given
        final int stringLength = 3;

        //when
        var namesFlux = testSubject.namesFluxMap(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxImmutability() {
        //given

        //when
        var namesFlux = testSubject.namesFluxImmutability();

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
        //given
        final int stringLength = 3;

        //when
        var namesMono = testSubject.namesMono_map_filter(stringLength);

        //then
        StepVerifier
                .create(namesMono)
                .expectNext("ALEX")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_default_is_empty() {
        //given
        final int stringLength = 4;

        //when
        var namesMono = testSubject.namesMono_map_filter(stringLength);

        //then
        StepVerifier
                .create(namesMono)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        //given
        final int stringLength = 3;

        //when
        var namesFlux = testSubject.namesFluxFlatMap(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        //given
        final int stringLength = 3;

        //when
        var namesFlux = testSubject.namesFluxFlatMapAsync(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxFlatConcatMap() {
        //given
        final int stringLength = 3;

        //when
        var namesFlux = testSubject.namesFluxFlatConcatMap(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatConcatMap_virtualTimer() {
        //given
        final int stringLength = 3;
        VirtualTimeScheduler.getOrSet();

        //when
        var namesFlux = testSubject.namesFluxFlatConcatMap(stringLength);

        //then
        StepVerifier
                .withVirtualTime(() -> namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        //given
        final int stringLength = 3;

        //when
        var namesMono = testSubject.namesMonoFlatMap(stringLength);

        //then
        StepVerifier
                .create(namesMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }
    @Test
    void namesMonoFlatMapMany() {
        //given
        final int stringLength = 3;

        //when
        var namesMono = testSubject.namesMonoFlatMapMany(stringLength);

        //then
        StepVerifier
                .create(namesMono)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        //given
        final int stringLength = 3;

        //when
        var namesFlux = testSubject.namesFluxTransform(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }
    @Test
    void namesFluxTransform1() {
        //given
        final int stringLength = 6;

        //when
        var namesFlux = testSubject.namesFluxTransform(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform_switchIfEmpty() {
        //given
        final int stringLength = 6;

        //when
        var namesFlux = testSubject.namesFluxTransform_switchIfEmpty(stringLength);

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_switchIfEmpty() {
        //given
        final int stringLength = 4;

        //when
        var concatFlux = testSubject.namesMono_map_filter_switchIfEmpty(stringLength);

        //then
        StepVerifier
                .create(concatFlux)
                .expectNext("DEFAULT")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        //given

        //when
        var namesFlux = testSubject.explore_concat();

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatwith() {
        //given

        //when
        var namesFlux = testSubject.explore_concatwith();

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatwith_mono() {
        //given

        //when
        var namesFlux = testSubject.explore_concatwith_mono();

        //then
        StepVerifier
                .create(namesFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        //given

        //when
        var value = testSubject.explore_merge();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith() {
        //given

        //when
        var value = testSubject.explore_mergeWith();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith_mono() {
        //given

        //when
        var value = testSubject.explore_mergeWith_mono();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        //given

        //when
        var value = testSubject.explore_mergeSequential();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        //given

        //when
        var value = testSubject.explore_zip();

        //then
        StepVerifier
                .create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zipWith() {
        //given

        //when
        var value = testSubject.explore_zipWith();

        //then
        StepVerifier
                .create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {
        //given

        //when
        var value = testSubject.explore_zip_1();

        //then
        StepVerifier
                .create(value)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }


    @Test
    void explore_zipWithMono() {
        //given

        //when
        var value = testSubject.explore_zipWithMono();

        //then
        StepVerifier
                .create(value)
                .expectNext("AB")
                .verifyComplete();
    }

    @Test
    void exception_flux() {
        //given

        //when
        var value = testSubject.exception_flux();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    void exception_flux_2() {
        //given

        //when
        var value = testSubject.exception_flux();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception occured")
                .verify();

    }

    @Test
    void explore_onErrorReturn() {
        //given

        //when
        var value = testSubject.explore_onErrorReturn();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void explore_onErrorResume() {
        //given
        final RuntimeException e = new IllegalStateException("Not a valid State!");

        //when
        var value = testSubject.explore_onErrorResume(e);

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_onErrorResume_2() {
        //given
        final RuntimeException e = new RuntimeException("Not a valid State!");

        //when
        var value = testSubject.explore_onErrorResume(e);

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void explore_onErrorContinue() {
        //given

        //when
        var value = testSubject.explore_onErrorContinue();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "C", "D")
                .verifyComplete();
    }

    @Test
    void explore_onErrorMap() {
        //given
        var e = new RuntimeException("Not a valid state");

        //when
        var value = testSubject.explore_onErrorMap(e);

        //then
        StepVerifier
                .create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_onErrorMap_reactorDebugAgent() {
        //given
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        var e = new RuntimeException("Not a valid state");

        //when
        var value = testSubject.explore_onErrorMap(e);

        //then
        StepVerifier
                .create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_onErrorMap_onOperatorDebug() {
        //given
        //Hooks.onOperatorDebug();
        var e = new RuntimeException("Not a valid state");

        //when
        var value = testSubject.explore_onErrorMap(e);

        //then
        StepVerifier
                .create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_doOnError() {
        //given

        //when
        var value = testSubject.explore_doOnError();

        //then
        StepVerifier
                .create(value)
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void explore_Mono_onErrorReturn() {
        //given

        //when
        var value = testSubject.explore_Mono_onErrorReturn();

        //then
        StepVerifier
                .create(value)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorMap() {
        //given
        final RuntimeException e = new RuntimeException("Not a valid State!");

        //when
        var value = testSubject.exception_mono_onErrorMap(e);

        //then
        StepVerifier
                .create(value)
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exception_mono_onErrorContinue() {
        //given
        final String value = "reactor";

        //when
        var result = testSubject.exception_mono_onErrorContinue(value);

        //then
        StepVerifier
                .create(result)
                .expectNext("abcd")
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorContinue_1() {
        //given
        final String value = "abc";

        //when
        var result = testSubject.exception_mono_onErrorContinue(value);

        //then
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void explor_generate() {
        //given

        //when
        var result = testSubject.explor_generate().log();

        //then
        StepVerifier
                .create(result)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void explore_create() {
        //given

        //when
        var result = testSubject.explore_create().log();

        //then
        StepVerifier
                .create(result)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void explore_create_mono() {
        //given

        //when
        var result = testSubject.explore_create_mono().log();

        //then
        StepVerifier
                .create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void explore_handle() {
        //given

        //when
        var result = testSubject.explore_handle().log();

        //then
        StepVerifier
                .create(result)
                .expectNextCount(2)
                .verifyComplete();
    }
}
