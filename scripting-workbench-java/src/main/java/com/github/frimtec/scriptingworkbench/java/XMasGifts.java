package com.github.frimtec.scriptingworkbench.java;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class XMasGifts {

    enum Participant {
        MARKUS,
        KARIN,
        LINA,
        MAYA,
        SOPHIE,
        EMMA
    }

    record GiftPair(Participant giver, Participant taker) {
    }

    record GiftEvent(LocalDate date, GiftPair pair) {
    }

    private static final int ROUNDS = 4;
    private static final Path OUTPUT_PATH = Paths.get("out").resolve("gifts");

    public static void main(String[] args) throws IOException {
        System.out.print("Searching for solution");
        Files.createDirectories(OUTPUT_PATH);
        Map<Participant, Path> outputFiles = Arrays.stream(Participant.values())
                .collect(
                        Collectors.toMap(
                                participant -> participant,
                                participant -> OUTPUT_PATH.resolve(participant.name() + ".txt")
                        )
                );
        outputFiles.values().forEach(path -> {
            try {
                Files.writeString(
                        path,
                        "",
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        boolean error;
        do {
            error = false;
            LinkedList<Participant> allTakers = new LinkedList<>(
                    IntStream.range(0, ROUNDS)
                            .mapToObj(i -> Arrays.stream(Participant.values()).toList())
                            .flatMap(List::stream)
                            .toList()
            );
            LinkedList<Participant> allGivers = new LinkedList<>(allTakers);
            Collections.shuffle(allTakers);
            Collections.shuffle(allGivers);
            Map<Participant, List<Participant>> pairing = new HashMap<>();
            Arrays.stream(Participant.values())
                    .forEach(giver -> pairing.put(giver, new ArrayList<>()));
            try {
                while (!allTakers.isEmpty()) {
                    Participant giver = allGivers.removeFirst();
                    List<Participant> takersOfGiver = pairing.get(giver);
                    takersOfGiver.add(nextSuitableTaker(giver, takersOfGiver, allTakers.removeFirst(), allTakers));
                }
                validateSolution(pairing);
                System.out.println();
                writeResult(pairing, getEventDates(), outputFiles);
            } catch (IllegalStateException e) {
                System.out.print(".");
                error = true;
            }
        } while (error);
        System.out.printf("Solution written to %s%n", OUTPUT_PATH.toAbsolutePath());
        String receipt = outputFiles.values().stream()
                .map(file -> "%-12s %s".formatted(file.getFileName(), calcHash(file)))
                .collect(Collectors.joining("\n"));
        Files.writeString(
                OUTPUT_PATH.resolve("receipt.txt"),
                receipt,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );
    }

    private static String calcHash(Path file) {
        try {
            return DigestUtils.sha256Hex(Files.readString(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeResult(
            Map<Participant, List<Participant>> pairing,
            LinkedList<LocalDate> eventDates,
            Map<Participant, Path> outputFiles
    ) {
        pairing.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(taker -> new GiftPair(entry.getKey(), taker)))
                .map(pair -> new GiftEvent(eventDates.removeFirst(), pair))
                .sorted(Comparator.comparing(GiftEvent::date))
                .forEach(event -> {
                    try {
                        Files.writeString(
                                outputFiles.get(event.pair().giver),
                                "%s %s\n".formatted(event.date().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), event.pair().taker()),
                                StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static LinkedList<LocalDate> getEventDates() {
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 1);
        LinkedList<LocalDate> eventDates = new LinkedList<>(
                IntStream.range(0, ROUNDS * Participant.values().length)
                        .mapToObj(startDate::plusDays)
                        .toList()
        );
        Collections.shuffle(eventDates);
        return eventDates;
    }

    private static void validateSolution(Map<Participant, List<Participant>> pairing) {
        Map<Participant, List<Participant>> entries = pairing.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(participant -> participant));

        if (!entries.keySet().containsAll(Arrays.stream(Participant.values()).toList())) {
            throw new RuntimeException("Inconsistent result");
        }
        entries.values().stream().mapToInt(List::size).forEach(size -> {
            if (size != ROUNDS) {
                throw new RuntimeException("Inconsistent result");
            }
        });
    }

    private static Participant nextSuitableTaker(Participant giver, List<Participant> takersOfGiver, Participant
            potentialTaker, LinkedList<Participant> restTakers) {
        int counter = 0;
        while (giver == potentialTaker || takersOfGiver.contains(potentialTaker)) {
            counter++;
            if (counter >= restTakers.size()) {
                throw new IllegalStateException("No solution found");
            }
            restTakers.addLast(potentialTaker);
            potentialTaker = restTakers.removeFirst();
        }
        return potentialTaker;
    }
}
