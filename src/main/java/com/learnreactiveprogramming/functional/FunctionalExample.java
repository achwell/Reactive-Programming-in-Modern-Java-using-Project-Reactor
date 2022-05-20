package com.learnreactiveprogramming.functional;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class FunctionalExample {

    public static void main(String[] args) {
        var nameList = List.of("alex", "ben", "chloe", "adam", "adam");
        var newNamesList = namesGreaterThanSize(nameList, 3);
        System.out.println(newNamesList);
    }

    private static List<String> namesGreaterThanSize(List<String> nameList, int i) {
        return nameList
                .parallelStream()
                .filter(name -> name.length() > 3)
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(toList());
    }}
