package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.just("alex", "ben", "chloe").log();
    }

    public Mono<String> namesMono() {
        return Mono.just("alex").log();
    }

    public Flux<String> namesFluxMap(int stringLength) {
        return Flux.just("alex", "ben", "chloe")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> System.out.println("Name is: " + name))
                .doOnSubscribe(subscription -> System.out.println("Subscription is" + subscription))
                .doOnComplete(() -> System.out.println("OnComplete"))
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<String> namesFluxImmutability() {
        var namesFlux = Flux.just("alex", "ben", "chloe");
        final Flux<String> map = namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono
                .just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<String> namesMono_map_filter_switchIfEmpty(int stringLength) {
        Function<Mono<String>, Mono<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        var defaultMono = Mono.just("default").transform(filterMap);
        return Mono
                .just("alex")
                .transform(filterMap)
                .switchIfEmpty(defaultMono)
                .log();
    }

    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono
                .just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono
                .just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    public Flux<String> namesFluxFlatMap(int stringLength) {
        return Flux.just("alex", "ben", "chloe")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        return Flux.just("alex", "ben", "chloe")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFluxFlatConcatMap(int stringLength) {
        return Flux.just("alex", "ben", "chloe")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase).filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxTransform_switchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default").transform(filterMap);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> explore_concat() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatwith() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");
        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concatwith_mono() {
        final Mono<String> aMono = Mono.just("A");
        final Mono<String> bMono = Mono.just("B");
        return aMono.concatWith(bMono).log();
    }

    public Flux<String> explore_merge() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        final Flux<String> defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeWith() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        final Flux<String> defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeSequential() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        final Flux<String> defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> explore_zip() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (s, s2) -> s + s2).log();
    }

    public Flux<String> explore_zip_1() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");
        final Flux<String> _123Flux = Flux.just("1", "2", "3");
        final Flux<String> _456Flux = Flux.just("4", "5", "6");
        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux).map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4()).log();
    }

    public Flux<String> explore_zipWith() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");
        return abcFlux.zipWith(defFlux, (s, s2) -> s + s2).log();
    }

    public Flux<String> explore_mergeWith_mono() {
        final Mono<String> aMono = Mono.just("A");
        final Mono<String> bMono = Mono.just("B");
        return aMono.mergeWith(bMono).log();
    }

    public Mono<String> explore_zipWithMono() {
        final Mono<String> aMono = Mono.just("A");
        final Mono<String> bMono = Mono.just("B");
        return aMono.zipWith(bMono).map(t1 -> t1.getT1() + t1.getT2()).log();
    }

    public Flux<String> exception_flux() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Flux.just("D"))
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<String> explore_onErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occured")))
                .onErrorReturn("D")
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<String> explore_onErrorResume(Exception e) {
        Flux<String> recoveryFlux = Flux.just("D", "E", "F");
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(throwable -> {
                    log.error("Exception is: ", throwable);
                    return throwable instanceof IllegalStateException ? recoveryFlux : Flux.error(throwable);
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<String> explore_onErrorContinue() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalStateException("Exception occured");
                    }
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((throwable, value) -> {
                    log.error("Exception is: ", throwable);
                    log.error("Name is: {}", value);
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<String> explore_onErrorMap(Exception e) {
        return /*Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalStateException("Exception occured");
                    }
                    return name;
                })
                .concatWith(Flux.just("D"))*/
                Flux.just("A")
                        .concatWith(Flux.error(e))
                        //.checkpoint("errorSpot")
                .onErrorMap(throwable -> {
                    log.error("Exception is: ", throwable);
                    return new ReactorException(throwable, throwable.getMessage());
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<String> explore_doOnError() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occured")))
                .doOnError(throwable -> {
                    log.error("Exception is", throwable);
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Mono<Object> explore_Mono_onErrorReturn() {
        return Mono.just("A")
                .map(s -> {
                    throw new RuntimeException("Exception occured");
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .onErrorReturn("abc")
                .log();
    }

    public Mono<Object> exception_mono_onErrorMap(RuntimeException e) {
        return Mono.just("B")
                .map(s -> {
                    throw e;
                })
                .onErrorMap(throwable -> {
                    System.out.println("Exception is: " + throwable);
                    return new ReactorException(throwable, throwable.getMessage());
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Mono<String> exception_mono_onErrorContinue(String value) {
        return Mono.just(value)
                .map(s -> {
                    if (s.equals("abc")) {
                        throw new RuntimeException();
                    }
                    return s;
                })
                .onErrorContinue((throwable, name) -> {
                    log.error("Exception is: ", throwable);
                    log.error("Name is: {}", name);
                })
                .doFinally(signalType -> System.out.println("Finally: " + signalType))
                .log();
    }

    public Flux<Integer> explor_generate() {
        return Flux.generate(() -> 1, (state, sink) -> {
            sink.next(state * 2);
            if (state == 10) {
                sink.complete();
            }
            return state + 1;
        });
    }


    public static List<String> names() {
        delay(1000);
        return List.of("alex", "ben", "chloe");
    }

    public Flux<String> explore_create() {
        return Flux.create(sink -> {
            //names().forEach(sink::next);
            CompletableFuture
                    .supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names.forEach(sink::next);
                    })
                    .thenRun(() -> sendEvents(sink))
            ;
        });
    }


    public void sendEvents(FluxSink<String> sink) {
        CompletableFuture
                .supplyAsync(() -> names())
                .thenAccept(names -> {
                    names.forEach((name) -> {
                        sink.next(name);
                        sink.next(name);
                    });
                })
                .thenRun(sink::complete);
    }


    public Mono<String> explore_create_mono() {
        return Mono.create(sink -> sink.success("alex"));
    }


    public Flux<String> explore_handle() {
        return Flux
                .fromIterable(List.of("alex", "ben", "chloe"))
                .handle((name, sink) -> {
                    if (name.length() > 3) {
                        sink.next(name.toUpperCase());
                    }
                });
    }

    private Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    private Flux<String> splitStringWithDelay(String name) {
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }
}
