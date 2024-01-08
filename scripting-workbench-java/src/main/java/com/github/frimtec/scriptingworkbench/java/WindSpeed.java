package com.github.frimtec.scriptingworkbench.java;

import java.util.stream.IntStream;

public class WindSpeed {

    public static final double RADIUS_IN_MILLI_METER = 38;
    public static final double IMPULSES_PER_ROTATION = 1;

    public static void main(String[] args) {
        IntStream.of(100, 1000, 1600, 2000, 5000, 10000).forEach(i -> System.out.printf("%d -> %.2f km/h%n", i , calculateWindSpeed(i)));
    }

    public static double calculateWindSpeed(long impulsePerMinutes) {
        return (2 * Math.PI * RADIUS_IN_MILLI_METER * (impulsePerMinutes * 60 / IMPULSES_PER_ROTATION)) / 1_000_000;
    }
}
