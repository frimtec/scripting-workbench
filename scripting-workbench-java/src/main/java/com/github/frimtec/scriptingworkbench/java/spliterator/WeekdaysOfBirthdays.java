package com.github.frimtec.scriptingworkbench.java.spliterator;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WeekdaysOfBirthdays {

    public static final LocalDate NEXT_BIRTHDAY = LocalDate.of(2023, Month.JUNE, 12);
    public static final int UNTIL_YEAR = 1972;

    public static void main(String[] args) {
        Stream<LocalDate> birthDayStream = StreamSupport.stream(
                new YearSpliterator(NEXT_BIRTHDAY),
                false
        );
        birthDayStream
                .takeWhile(day -> day.getYear() >= UNTIL_YEAR)
                .map(day -> day + " -> " + day.getDayOfWeek())
                .forEach(System.out::println);
    }
}
