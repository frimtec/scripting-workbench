package com.github.frimtec.scriptingworkbench.java;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Wichteln {

    enum Participant {
        MARKUS,
        KARIN,
        LINA,
        MAYA,
        SOPHIE,
        EMMA,
        ERNST,
        TRUDI,
        JUERG,
        MELANIE,
        LIONEL,
        EILEEN
    }

    private static final Path OUTPUT_PATH = Paths.get("out").resolve("wichtel-pairing");

    private static final Random RND = new Random();

    private record Pair(Participant participant, Participant recipient) {
    }

    public static void main(String[] args) throws IOException {
        System.out.print("Searching for solution ");

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

            List<Pair> pairs = generatePairs();
            try {
                validateSolution(pairs);
                System.out.println();
                writeResult(pairs, outputFiles);
            } catch (IllegalStateException e) {
                System.out.print(".");
                error = true;
            }
        } while (error);
        System.out.printf("\nSolution written to %s%n\n", OUTPUT_PATH.toAbsolutePath());
        String receipt = outputFiles.values().stream()
                .map(file -> "%-12s %s".formatted(file.getFileName(), calcHash(file)))
                .collect(Collectors.joining("\n"));
        Files.writeString(
                OUTPUT_PATH.resolve("receipt.txt"),
                "SHA256 Calculator (https://emn178.github.io/online-tools/sha256.html)\n\n" + receipt,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
        );
    }

    private static void writeResult(
            List<Pair> pairing,
            Map<Participant, Path> outputFiles
    ) {
        pairing.forEach(pair -> {
            try {
                Files.writeString(
                        outputFiles.get(pair.participant()),
                        "%s [%08d]\n".formatted(pair.recipient(), RND.nextLong(99999999L)),
                        StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void validateSolution(List<Pair> pairs) {
        if (pairs.stream().map(pair -> pair.participant).distinct().count() != Participant.values().length) {
            throw new IllegalStateException("Participant count mismatch");
        }
        if (pairs.stream().map(pair -> pair.recipient).distinct().count() != Participant.values().length) {
            throw new IllegalStateException("Recipient count mismatch");
        }
        pairs.stream()
                .filter(pair -> pair.participant == pair.recipient)
                .findAny()
                .ifPresent(pair -> {
                    throw new IllegalStateException("Self assignment");
                });
    }

    private static List<Pair> generatePairs() {
        List<Participant> participants = new ArrayList<>(List.of(Participant.values()));
        Collections.shuffle(participants, RND);
        List<Participant> recipients = new ArrayList<>(participants);
        Collections.shuffle(recipients, RND);
        return participants.stream()
                .map(participant -> new Pair(participant, recipients.remove(0)))
                .toList();
    }

    private static String calcHash(Path file) {
        try {
            return DigestUtils.sha256Hex(Files.readString(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}