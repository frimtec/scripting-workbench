package com.github.frimtec.scriptingworkbench.java;

import java.util.Arrays;
import java.util.Random;

public class Lotto {
    public static void main(String[] args) {
        var random = new Random();
        var numbers = new int[0];
        while (numbers.length != 6) {
            numbers = random.ints(1, 42 + 1).limit(6).sorted().distinct().toArray();
        }
        System.out.print("Zahlen: ");
        Arrays.stream(numbers).forEach(n -> System.out.print("" + n + " "));

        System.out.println();
        System.out.print("Glückszahl:" + random.ints(1, 6 + 1).limit(1).findAny().orElseThrow(() -> new RuntimeException("x")));
    }
}

